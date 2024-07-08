package com.tien.profile.service;

import com.tien.profile.dto.request.ProductCreationRequest;
import com.tien.profile.dto.request.ProductUpdateRequest;
import com.tien.profile.dto.response.ProductResponse;
import com.tien.profile.entity.Product;
import com.tien.profile.exception.AppException;
import com.tien.profile.exception.ErrorCode;
import com.tien.profile.mapper.ProductMapper;
import com.tien.profile.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

      ProductRepository productRepository;
      ProductMapper productMapper;

      @PreAuthorize("hasRole('ADMIN')")
      @CachePut(value = "products", key = "#result.id")
      @CacheEvict(value = "allProducts", allEntries = true)
      @Transactional
      public ProductResponse createProduct(ProductCreationRequest request) {
            Product product = productMapper.toProduct(request);
            return productMapper.toProductResponse(productRepository.save(product));
      }

      @PreAuthorize("hasRole('ADMIN')")
      @CachePut(value = "products", key = "#productId")
      @CacheEvict(value = "allProducts", allEntries = true)
      @Transactional
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            productMapper.updateProduct(product, request);
            return productMapper.toProductResponse(productRepository.save(product));
      }

      @Cacheable(value = "allProducts")
      public List<ProductResponse> getAllProducts() {
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

//      @Cacheable(value = "products", key = "#productId")
      public ProductResponse getProductById(String productId) {
            return productMapper.toProductResponse(productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      }

      @PreAuthorize("hasRole('ADMIN')")
      @CacheEvict(value = "allProducts", allEntries = true)
      @Transactional
      public void deleteProduct(String productId) {
            productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            productRepository.deleteById(productId);
      }

}