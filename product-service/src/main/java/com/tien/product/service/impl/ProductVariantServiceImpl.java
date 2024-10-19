package com.tien.product.service.impl;

import com.tien.product.dto.request.ProductVariantRequest;
import com.tien.product.dto.request.ProductVariantUpdateRequest;
import com.tien.product.entity.Product;
import com.tien.product.entity.ProductVariant;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import com.tien.product.mapper.ProductVariantMapper;
import com.tien.product.repository.ProductRepository;
import com.tien.product.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantServiceImpl implements ProductVariantService {

      ProductRepository productRepository;
      ProductVariantMapper productVariantMapper;

      @Override
      @Transactional
      public void addProductVariant(String productId, ProductVariantRequest request) {
            Product product = findProductById(productId);
            ProductVariant newVariant = productVariantMapper.toProductVariant(request);
            product.getVariants().add(newVariant);
            productRepository.save(product);
      }

      @Override
      @Transactional
      public void updateProductVariant(String productId, String variantId, ProductVariantUpdateRequest request) {
            Product product = findProductById(productId);
            ProductVariant variant = findProductVariant(product, variantId);
            productVariantMapper.updateProductVariant(variant, request);
            productRepository.save(product);
      }

      @Override
      @Transactional
      public void updateStockAndSoldQuantity(String productId, String variantId, int quantity) {
            Product product = findProductById(productId);
            ProductVariant variant = findProductVariant(product, variantId);

            int newStock = variant.getStock() - quantity;
            if (newStock < 0) {
                  throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            variant.setStock(newStock);
            product.setSoldQuantity(product.getSoldQuantity() + quantity);
            productRepository.save(product);
      }

      @Override
      @Transactional
      public void deleteProductVariant(String productId, String variantId) {
            Product product = findProductById(productId);
            ProductVariant variant = findProductVariant(product, variantId);

            product.getVariants().remove(variant);

            productRepository.save(product);
      }

      private Product findProductById(String productId) {
            return productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
      }

      private ProductVariant findProductVariant(Product product, String variantId) {
            return product.getVariants().stream()
                    .filter(v -> v.getVariantId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
      }

}