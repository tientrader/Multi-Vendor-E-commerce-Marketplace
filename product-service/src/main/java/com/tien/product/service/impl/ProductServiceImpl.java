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

      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request) {
            String username = getCurrentUsername();
            log.info("Creating product for shop owner: {}", username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(createProduct) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                          log.error("(createProduct) Category not found with ID: {}", request.getCategoryId());
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });

            if (!category.getShopId().equals(shopResponse.getId())) {
                  log.error("Unauthorized product creation attempt for category: {}", request.getCategoryId());
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            Product product = productMapper.toProduct(request);
            product.setCategory(category);
            product = productRepository.save(product);

            category.getProducts().add(product);
            categoryRepository.save(category);

            log.info("Product created with ID: {}", product.getId());
            return productMapper.toProductResponse(product);
      }

      public Page<ProductResponse> searchProducts(
              int page, int size, String sortBy, String sortDirection,
              String categoryId, Double minPrice, Double maxPrice) {

            Criteria criteria = new Criteria();

            Optional.ofNullable(categoryId).ifPresent(id -> criteria.and("categoryId").is(id));

            if (minPrice != null && maxPrice != null) {
                  criteria.and("price").gte(minPrice).lte(maxPrice);
            } else if (minPrice != null) {
                  criteria.and("price").gte(minPrice);
            } else if (maxPrice != null) {
                  criteria.and("price").lte(maxPrice);
            }

            Query query = Query.query(criteria);
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            query.with(pageable);

            List<Product> products = mongoTemplate.find(query, Product.class);
            long total = mongoTemplate.count(query, Product.class);
            List<ProductResponse> productResponses = productMapper.toProductResponses(products);

            log.info("Found {} products", productResponses.size());
            return new PageImpl<>(productResponses, pageable, total);
      }

      public List<ProductResponse> getAllProducts() {
            log.info("Fetching all products");
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      public ProductResponse getProductById(String productId) {
            log.info("Fetching product with ID: {}", productId);
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }));
      }

      public double getProductPriceById(String productId) {
            log.info("Fetching price for product with ID: {}", productId);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(getProductPriceById) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });
            return product.getPrice();
      }

      public ExistsResponse existsProduct(String productId) {
            log.info("Checking existence for product with ID: {}", productId);
            boolean exists = productRepository.existsById(productId);
            return new ExistsResponse(exists);
      }

      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            String username = getCurrentUsername();
            log.info("Updating product with ID: {} by shop owner: {}", productId, username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(updateProduct) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(updateProduct) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                          log.error("(updateProduct) Category not found with ID: {}", request.getCategoryId());
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });

            if (!newCategory.getShopId().equals(shopResponse.getId())) {
                  log.error("Unauthorized update attempt for product ID: {} to category ID: {}", productId, request.getCategoryId());
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

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

      @Transactional
      public void updateStock(String productId, int quantity) {
            log.info("Updating stock for product with ID: {}. Quantity: {}", productId, quantity);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(updateStock) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            int newStock = product.getStock() + quantity;
            if (newStock <= 0) {
                  log.error("Attempted to reduce stock below zero for product ID: {}", productId);
                  throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            product.setStock(newStock);
            productRepository.save(product);
            log.info("Stock updated for product ID: {}. New stock: {}", productId, newStock);
      }

      @Transactional
      public void deleteProduct(String productId) {
            String username = getCurrentUsername();
            log.info("Deleting product with ID: {} by shop owner: {}", productId, username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(deleteProduct) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                          log.error("(deleteProduct) Product not found with ID: {}", productId);
                          return new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    });

            if (!product.getCategory().getShopId().equals(shopResponse.getId())) {
                  log.error("Unauthorized delete attempt for product ID: {}", productId);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

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

      private Optional<ShopResponse> getShopByOwnerUsername(String username) {
            return Optional.ofNullable(shopClient.getShopByOwnerUsername(username).getResult());
      }

}