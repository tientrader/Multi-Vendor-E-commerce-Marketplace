package com.tien.product.service.impl;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.FileResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.entity.Product;
import com.tien.product.entity.ProductVariant;
import com.tien.product.enums.ProductSort;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.httpclient.FileClient;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.mapper.ProductVariantMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.service.ProductService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      ProductVariantMapper productVariantMapper;
      CategoryRepository categoryRepository;
      ShopClient shopClient;
      FileClient fileClient;
      MongoTemplate mongoTemplate;
      RedisTemplate<String, Object> redisTemplate;

      @Override
      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request, List<MultipartFile> productImages) {
            if (productImages == null || productImages.isEmpty()) {
                  log.error("Product images are required but none were provided.");
                  throw new AppException(ErrorCode.IMAGE_REQUIRED);
            }

            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);
            Category category = findCategoryById(request.getCategoryId());
            checkCategoryOwnership(category, shopResponse.getId());

            Product product = productMapper.toProduct(request);
            product.setCategoryId(category.getId());
            product.setShopId(shopResponse.getId());

            List<String> imageUrls = handleImageUpload(productImages);
            product.setImageUrls(imageUrls);

            List<ProductVariant> variants = request.getVariants().stream()
                    .map(productVariantMapper::toProductVariant)
                    .toList();
            product.setVariants(variants);

            product = productRepository.save(product);

            category.getProductIds().add(product.getId());
            categoryRepository.save(category);

            redisTemplate.opsForValue().set("product:" + product.getId(), product);

            return productMapper.toProductResponse(product);
      }

      @Override
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request, List<MultipartFile> productImages) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Product product = findProductById(productId);
            Category newCategory = findCategoryById(request.getCategoryId());
            checkCategoryOwnership(newCategory, shopResponse.getId());

            Category oldCategory = findCategoryById(product.getCategoryId());
            oldCategory.getProductIds().remove(product.getId());
            categoryRepository.save(oldCategory);

            productMapper.updateProduct(product, request);
            product.setCategoryId(newCategory.getId());

            if (productImages != null && !productImages.isEmpty()) {
                  List<String> imageUrls = handleImageUpload(productImages);
                  product.setImageUrls(imageUrls);
            }

            product = productRepository.save(product);

            newCategory.getProductIds().add(product.getId());
            categoryRepository.save(newCategory);

            redisTemplate.opsForValue().set("product:" + product.getId(), product);

            return productMapper.toProductResponse(product);
      }

      @Override
      @Transactional
      public void deleteProduct(String productId) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Product product = findProductById(productId);
            checkCategoryOwnership(findCategoryById(product.getCategoryId()), shopResponse.getId());

            Category category = findCategoryById(product.getCategoryId());
            category.getProductIds().remove(product.getId());
            categoryRepository.save(category);

            productRepository.deleteById(productId);
            redisTemplate.delete("product:" + productId);
      }

      @Override
      public Page<ProductResponse> getProducts(
              String shopId, String categoryId,
              int page, int size, String sortBy, String sortDirection,
              Double minPrice, Double maxPrice, ProductSort productSort) {

            productSort = Optional.ofNullable(productSort).orElse(ProductSort.DEFAULT);
            Criteria criteria = new Criteria();

            if (shopId != null) {
                  criteria.and("shopId").is(shopId);
            }
            if (categoryId != null) {
                  criteria.and("category.id").is(categoryId);
            }
            if (minPrice != null && maxPrice != null) {
                  criteria.and("price").gte(minPrice).lte(maxPrice);
            } else if (minPrice != null) {
                  criteria.and("price").gte(minPrice);
            } else if (maxPrice != null) {
                  criteria.and("price").lte(maxPrice);
            }

            Sort sort = switch (productSort) {
                  case BEST_SELLING -> Sort.by(Sort.Direction.DESC, "soldQuantity");
                  case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
                  default -> Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            };

            Query query = Query.query(criteria).with(PageRequest.of(page, size, sort));
            List<Product> products = mongoTemplate.find(query, Product.class);
            long total = mongoTemplate.count(query.skip(-1).limit(-1), Product.class);
            List<ProductResponse> responses = productMapper.toProductResponses(products);

            return new PageImpl<>(responses, PageRequest.of(page, size), total);
      }

      @Override
      public ProductResponse getProductById(String productId) {
            return productMapper.toProductResponse(findProductById(productId));
      }

      @Override
      public List<ProductResponse> getProductsByShopId(String shopId) {
            return productMapper.toProductResponses(productRepository.findByShopId(shopId));
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<ProductResponse> getAllProducts() {
            return productMapper.toProductResponses(productRepository.findAll());
      }

      @Override
      public double getProductPriceById(String productId, String variantId) {
            Product product = findProductById(productId);
            ProductVariant variant = findVariantById(product, variantId);
            return variant.getPrice();
      }

      @Override
      public int getProductStockById(String productId, String variantId) {
            Product product = findProductById(productId);
            ProductVariant variant = findVariantById(product, variantId);
            return variant.getStock();
      }

      @Override
      public ExistsResponse existsProduct(String productId, String variantId) {
            Product product = findProductById(productId);
            boolean variantExists = product.getVariants().stream()
                    .anyMatch(variant -> variant.getVariantId().equals(variantId));
            return new ExistsResponse(variantExists);
      }

      @Override
      public String getShopIdByProductId(String productId) {
            Product product = findProductById(productId);
            return product.getShopId();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private List<String> handleImageUpload(List<MultipartFile> productImages) {
            if (productImages == null || productImages.isEmpty()) {
                  return List.of();
            }

            ApiResponse<List<FileResponse>> fileResponseApi;
            try {
                  fileResponseApi = fileClient.uploadMultipleFiles(productImages);

                  if (fileResponseApi == null || fileResponseApi.getResult() == null) {
                        log.error("File upload returned no results");
                        throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                  }
            } catch (FeignException e) {
                  log.error("Error uploading files: {}", e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            List<FileResponse> fileResponses = fileResponseApi.getResult();

            return fileResponses.stream()
                    .map(FileResponse::getUrl)
                    .collect(Collectors.toList());
      }

      private Product findProductById(String productId) {
            Product product = (Product) redisTemplate.opsForValue().get("product:" + productId);
            if (product == null) {
                  product = productRepository.findById(productId)
                          .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                  redisTemplate.opsForValue().set("product:" + productId, product);
            }
            return product;
      }

      private ProductVariant findVariantById(Product product, String variantId) {
            return product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> {
                          log.error("Variant not found with ID: {} for product ID: {}", variantId, product.getId());
                          return new AppException(ErrorCode.VARIANT_NOT_FOUND);
                    });
      }

      private Category findCategoryById(String categoryId) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                          log.error("Category not found with ID: {}", categoryId);
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });
      }

      private void checkCategoryOwnership(Category category, String shopId) {
            if (!category.getShopId().equals(shopId)) {
                  log.error("Unauthorized access to category ID: {} by shop ID: {}", category.getId(), shopId);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            try {
                  ApiResponse<ShopResponse> response = shopClient.getShopByOwnerUsername(username);

                  return Optional.ofNullable(response.getResult())
                          .orElseThrow(() -> {
                                log.error("Shop not found for username: {}", username);
                                return new AppException(ErrorCode.SHOP_NOT_FOUND);
                          });
            } catch (FeignException e) {
                  log.error("Error calling shop service for username {}: {}", username, e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

}