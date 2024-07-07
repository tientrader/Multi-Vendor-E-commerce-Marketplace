package com.tien.profile.controller;

import com.tien.profile.dto.ApiResponse;
import com.tien.profile.dto.request.ProductCreationRequest;
import com.tien.profile.dto.request.ProductUpdateRequest;
import com.tien.profile.dto.response.ProductResponse;
import com.tien.profile.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

      ProductService productService;

      @PostMapping()
      ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.createProduct(request))
                    .build();
      }

      @PutMapping("/{productId}")
      ApiResponse<ProductResponse> updateProduct(@PathVariable String productId, @RequestBody @Valid ProductUpdateRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(productId, request))
                    .build();
      }

      @GetMapping("/{productId}")
      ApiResponse<ProductResponse> getProductById(@PathVariable("productId") String productId) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.getProductById(productId))
                    .build();
      }

      @GetMapping
      ApiResponse<List<ProductResponse>> getAllProducts() {
            return ApiResponse.<List<ProductResponse>>builder()
                    .result(productService.getAllProducts())
                    .build();
      }

      @DeleteMapping("/{productId}")
      ApiResponse<String> deleteProduct(@PathVariable String productId) {
            productService.deleteProduct(productId);
            return ApiResponse.<String>builder()
                    .result("Product has been deleted")
                    .build();
      }

}