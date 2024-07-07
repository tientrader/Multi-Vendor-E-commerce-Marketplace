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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
      
      ProductRepository productRepository;
      ProductMapper productMapper;

      public ProductResponse createProduct(ProductCreationRequest request) {
            Product product = productMapper.toProduct(request);
            return productMapper.toProductResponse(productRepository.save(product));
      }

      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            productMapper.updateProduct(product, request);
            return productMapper.toProductResponse(productRepository.save(product));
      }

      public List<ProductResponse> getAllProducts() {
            return productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
      }

      public ProductResponse getProductById(String id) {
            return productRepository.findById(id)
                    .map(productMapper::toProductResponse)
                    .orElse(null);
      }

      public void deleteProduct(String id) {
            productRepository.deleteById(id);
      }

}