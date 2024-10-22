package com.tien.product.service.impl;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.entity.Product;
import com.tien.product.entity.ProductVariant;
import com.tien.product.enums.ProductSort;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.mapper.ProductVariantMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.service.ProductService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
      MongoTemplate mongoTemplate;

      @Override
      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);
            Category category = findCategoryById(request.getCategoryId());
            checkCategoryOwnership(category, shopResponse.getId());

            Product product = productMapper.toProduct(request);
            product.setCategory(category);
            product.setShopId(shopResponse.getId());

            List<ProductVariant> variants = request.getVariants().stream()
                    .map(productVariantMapper::toProductVariant)
                    .collect(Collectors.toList());
            product.setVariants(variants);

            product = productRepository.save(product);

            category.getProducts().add(product);
            categoryRepository.save(category);

            return productMapper.toProductResponse(product);
      }

      @Override
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Product product = findProductById(productId);
            Category newCategory = findCategoryById(request.getCategoryId());
            checkCategoryOwnership(newCategory, shopResponse.getId());

            Category oldCategory = product.getCategory();
            oldCategory.getProducts().remove(product);
            categoryRepository.save(oldCategory);

            productMapper.updateProduct(product, request);
            product.setCategory(newCategory);
            product = productRepository.save(product);

            newCategory.getProducts().add(product);
            categoryRepository.save(newCategory);

            return productMapper.toProductResponse(product);
      }

      @Override
      @Transactional
      public void deleteProduct(String productId) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Product product = findProductById(productId);
            checkCategoryOwnership(product.getCategory(), shopResponse.getId());

            Category category = product.getCategory();
            category.getProducts().remove(product);
            categoryRepository.save(category);

            productRepository.deleteById(productId);
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
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      }

      @Override
      public List<ProductResponse> getAllProducts() {
            return productMapper.toProductResponses(productRepository.findAll());
      }

      @Override
      public double getProductPriceById(String productId, String variantId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            ProductVariant variant = product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

            return variant.getPrice();
      }

      @Override
      public int getProductStockById(String productId, String variantId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            ProductVariant variant = product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

            return variant.getStock();
      }

      @Override
      public ExistsResponse existsProduct(String productId, String variantId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            boolean variantExists = product.getVariants().stream()
                    .anyMatch(variant -> variant.getVariantId().equals(variantId));

            return new ExistsResponse(variantExists);
      }

      @Override
      public String getShopIdByProductId(String productId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            return product.getShopId();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Product findProductById(String productId) {
            return productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
      }

      private Category findCategoryById(String categoryId) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
      }

      private void checkCategoryOwnership(Category category, String shopId) {
            if (!category.getShopId().equals(shopId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            return Optional.ofNullable(shopClient.getShopByOwnerUsername(username).getResult())
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
      }

}