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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {

      ProductRepository productRepository;
      ProductMapper productMapper;
      RedisTemplate<String, List<ProductResponse>> listRedisTemplate;
      RedisTemplate<String, ProductResponse> redisTemplate;

      @PreAuthorize("hasRole('ADMIN')")
      public ProductResponse createProduct(ProductCreationRequest request) {
            Product product = productMapper.toProduct(request);
            ProductResponse productResponse = productMapper.toProductResponse(productRepository.save(product));

            String productId = productResponse.getId();
            redisTemplate.opsForValue().set("product_" + productId, productResponse);

            List<ProductResponse> cachedProducts = listRedisTemplate.opsForValue().get("all_products");
            if (cachedProducts != null) {
                  cachedProducts.add(productResponse);
                  listRedisTemplate.opsForValue().set("all_products", cachedProducts);
            }

            return productResponse;
      }

      @PreAuthorize("hasRole('ADMIN')")
      public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            productMapper.updateProduct(product, request);
            ProductResponse updatedProduct = productMapper.toProductResponse(productRepository.save(product));

            redisTemplate.opsForValue().set("product_" + productId, updatedProduct, 1, TimeUnit.HOURS);

            List<ProductResponse> productResponses = productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
            listRedisTemplate.opsForValue().set("all_products", productResponses);

            return updatedProduct;
      }

      public List<ProductResponse> getAllProducts() {
            List<ProductResponse> cachedProducts = listRedisTemplate.opsForValue().get("all_products");
            if (cachedProducts != null) return cachedProducts;

            List<ProductResponse> productResponses = productRepository.findAll().stream()
                    .map(productMapper::toProductResponse)
                    .collect(Collectors.toList());
            listRedisTemplate.opsForValue().set("all_products", productResponses);

            return productResponses;
      }

      public ProductResponse getProductById(String id) {
            ProductResponse cachedProduct = redisTemplate.opsForValue().get("product_" + id);
            if (cachedProduct != null) return cachedProduct;

            return productMapper.toProductResponse(productRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      }

      @PreAuthorize("hasRole('ADMIN')")
      public void deleteProduct(String id) {
            productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            productRepository.deleteById(id);

            redisTemplate.delete("product_" + id);
            List<ProductResponse> cachedProducts = listRedisTemplate.opsForValue().get("all_products");
            if (cachedProducts != null) {
                  cachedProducts = cachedProducts.stream()
                          .filter(product -> !product.getId().equals(id))
                          .collect(Collectors.toList());
                  listRedisTemplate.opsForValue().set("all_products", cachedProducts, 1, TimeUnit.HOURS);
            }
      }

}