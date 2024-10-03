package com.tien.product.service.impl;

import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.entity.Product;
import com.tien.product.entity.ProductVariant;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.service.ProductCommandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCommandServiceImpl implements ProductCommandService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      CategoryRepository categoryRepository;
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
      public void updateStockAndSoldQuantity(String productId, String variantId, int quantity) {
            log.info("Updating stock for variant with ID: {} for product ID: {}. Quantity: {}", variantId, productId, quantity);

            Product product = findProductById(productId);
            ProductVariant variant = product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> {
                          log.error("(updateStockAndSoldQuantity) Variant not found with ID: {}", variantId);
                          return new AppException(ErrorCode.VARIANT_NOT_FOUND);
                    });

            int newStock = variant.getStock() - quantity;
            if (newStock < 0) {
                  log.error("Attempted to reduce stock below zero for variant ID: {}", variantId);
                  throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            variant.setStock(newStock);

            product.setSoldQuantity(product.getSoldQuantity() + quantity);

            productRepository.save(product);

            log.info("Stock updated for variant ID: {}. New stock: {}", variantId, newStock);
            log.info("Sold quantity updated for product ID: {}. New sold quantity: {}", productId, product.getSoldQuantity());
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

}