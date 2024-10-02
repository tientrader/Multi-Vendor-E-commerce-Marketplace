package com.tien.product.service.impl;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
      CategoryRepository categoryRepository;
      MongoTemplate mongoTemplate;
      ShopClient shopClient;

      @Override
      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request) {
            String username = getCurrentUsername();
            log.info("Creating product for shop owner: {}", username);

            ShopResponse shopResponse = getShopByOwnerUsername(username);
            Category category = findCategoryById(request.getCategoryId());
            checkCategoryOwnership(category, shopResponse.getId());

            Product product = productMapper.toProduct(request);
            product.setCategory(category);
            product.setShopId(shopResponse.getId());
            product.setCreatedAt(LocalDateTime.now());
            product = productRepository.save(product);

            category.getProducts().add(product);
            categoryRepository.save(category);

            log.info("Product created with ID: {}", product.getId());
            return productMapper.toProductResponse(product);
      }

      @Override
      public Page<ProductResponse> getProducts(
              String shopId, String categoryId,
              int page, int size, String sortBy, String sortDirection,
              Double minPrice, Double maxPrice, ProductSort productSort) {

            if (productSort == null) {
                  productSort = ProductSort.DEFAULT;
            }

            Criteria criteria = new Criteria();

            if (shopId != null) {
                  criteria.and("shopId").is(shopId);
                  log.info("Fetching products for shopId: {}", shopId);
            } else if (categoryId != null) {
                  criteria.and("category.id").is(categoryId);
                  log.info("Fetching products for categoryId: {}", categoryId);
            } else {
                  log.info("Fetching products for homepage");
            }

            addPriceCriteria(criteria, minPrice, maxPrice);

            Sort sort;
            if (productSort == ProductSort.BEST_SELLING) {
                  sort = Sort.by(Sort.Direction.DESC, "soldQuantity");
            } else if (productSort == ProductSort.NEWEST) {
                  sort = Sort.by(Sort.Direction.DESC, "createdAt");
            } else {
                  sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            }

            return getProductsPage(criteria, page, size, sort);
      }

      @Override
      public List<ProductResponse> getAllProducts() {
            log.info("Fetching all products");
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public ProductResponse getProductById(String productId) {
            log.info("Fetching product with ID: {}", productId);
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }));
      }

      @Override
      public double getProductPriceById(String productId) {
            log.info("Fetching price for product with ID: {}", productId);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductPriceById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });
            return product.getPrice();
      }

      @Override
      public ExistsResponse existsProduct(String productId) {
            log.info("Checking existence for product with ID: {}", productId);
            boolean exists = productRepository.existsById(productId);
            return new ExistsResponse(exists);
      }

      @Override
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            String username = getCurrentUsername();
            log.info("Updating product with ID: {} by shop owner: {}", productId, username);

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

            log.info("Product updated with ID: {}", product.getId());
            return productMapper.toProductResponse(product);
      }

      @Override
      @Transactional
      public void updateStockAndSoldQuantity(String productId, int quantity) {
            log.info("Updating stock and sold quantity for product with ID: {}. Quantity: {}", productId, quantity);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(updateStockAndSoldQuantity) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            int newStock = product.getStock() - quantity;
            if (newStock < 0) {
                  log.error("Attempted to reduce stock below zero for product ID: {}", productId);
                  throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            product.setStock(newStock);
            product.setSoldQuantity(product.getSoldQuantity() + quantity);

            productRepository.save(product);

            log.info("Stock and sold quantity updated for product ID: {}. New stock: {}, Sold quantity: {}",
                    productId, newStock, product.getSoldQuantity());
      }

      @Override
      @Transactional
      public void deleteProduct(String productId) {
            String username = getCurrentUsername();
            log.info("Deleting product with ID: {} by shop owner: {}", productId, username);

            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Product product = findProductById(productId);
            checkCategoryOwnership(product.getCategory(), shopResponse.getId());

            Category category = product.getCategory();
            category.getProducts().remove(product);
            categoryRepository.save(category);

            productRepository.deleteById(productId);
            log.info("Product deleted with ID: {}", productId);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            return Optional.ofNullable(shopClient.getShopByOwnerUsername(username).getResult())
                    .orElseThrow(() -> {
                          log.error("Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });
      }

      private Product findProductById(String productId) {
            return productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(findProductById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });
      }

      private Category findCategoryById(String categoryId) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                          log.error("(findCategoryById) Category not found with ID: {}", categoryId);
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });
      }

      private void checkCategoryOwnership(Category category, String shopId) {
            if (!category.getShopId().equals(shopId)) {
                  log.error("Unauthorized access attempt for category: {}", category.getId());
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
      }

      private void addPriceCriteria(Criteria criteria, Double minPrice, Double maxPrice) {
            if (minPrice != null && maxPrice != null) {
                  criteria.and("price").gte(minPrice).lte(maxPrice);
            } else if (minPrice != null) {
                  criteria.and("price").gte(minPrice);
            } else if (maxPrice != null) {
                  criteria.and("price").lte(maxPrice);
            }
      }

      private Page<ProductResponse> getProductsPage(
              Criteria criteria, int page, int size, Sort sort) {

            Query query = Query.query(criteria);
            Pageable pageable = PageRequest.of(page, size, sort);
            query.with(pageable);

            List<Product> products = mongoTemplate.find(query, Product.class);
            long total = mongoTemplate.count(query, Product.class);
            List<ProductResponse> productResponses = productMapper.toProductResponses(products);

            return new PageImpl<>(productResponses, pageable, total);
      }

}