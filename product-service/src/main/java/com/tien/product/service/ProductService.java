package com.tien.product.service;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.entity.Category;
import com.tien.product.mapper.ProductMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.repository.ProductRepository;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      CategoryRepository categoryRepository;

      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public ProductResponse addProduct(ProductCreationRequest request) {
            Product product = productMapper.toProduct(request);
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            product.setCategory(category);
            product = productRepository.save(product);

            category.getProducts().add(product);
            categoryRepository.save(category);

            return productMapper.toProductResponse(product);
      }

      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

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

      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public void deleteProduct(String productId) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Category category = product.getCategory();
            category.getProducts().remove(product);
            categoryRepository.save(category);

            productRepository.deleteById(productId);
      }

      public List<ProductResponse> getAllProducts() {
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      public ProductResponse getProductById(String productId) {
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      }

      @Transactional
      public void updateStock(String productId, int quantity) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            int updatedStock = product.getStock() - quantity;
            if (updatedStock < 0) {
                  throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            product.setStock(updatedStock);
            productRepository.save(product);
      }

}