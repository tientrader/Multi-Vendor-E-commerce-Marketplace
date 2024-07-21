package com.tien.product.controller;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

      ProductService productService;

      @PostMapping
      ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.createProduct(request))
                    .build();
      }

      @PutMapping("/{productId}")
      ApiResponse<ProductResponse> updateProduct(@PathVariable String productId,
                                                 @RequestBody @Valid ProductUpdateRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(productId, request))
                    .build();
      }

      @PutMapping("/{productId}/stock")
      ApiResponse<Void> updateStock(@PathVariable String productId, @RequestParam int quantity) {
            productService.updateStock(productId, quantity);
            return ApiResponse.<Void>builder()
                    .message("Stock updated successfully")
                    .build();
      }

      @DeleteMapping("/{productId}")
      ApiResponse<String> deleteProduct(@PathVariable String productId) {
            productService.deleteProduct(productId);
            return ApiResponse.<String>builder()
                    .result("Product has been deleted")
                    .build();
      }

      @GetMapping
      ApiResponse<Page<ProductResponse>> getProductsWithPaginationAndSorting(
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "name") String sortBy,
              @RequestParam(defaultValue = "asc") String sortDirection) {
            Page<ProductResponse> productsPage = productService.getProductsWithPaginationAndSorting
                    (page, size, sortBy, sortDirection);
            return ApiResponse.<Page<ProductResponse>>builder()
                    .result(productsPage)
                    .build();
      }

      @GetMapping("/{productId}")
      ApiResponse<ProductResponse> getProductById(@PathVariable("productId") String productId) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.getProductById(productId))
                    .build();
      }

      @GetMapping("/all")
      ApiResponse<List<ProductResponse>> getAllProducts() {
            return ApiResponse.<List<ProductResponse>>builder()
                    .result(productService.getAllProducts())
                    .build();
      }

      @GetMapping("/{productId}/price")
      ApiResponse<Double> getProductPriceById(@PathVariable String productId) {
            double price = productService.getProductPriceById(productId);
            return ApiResponse.<Double>builder()
                    .result(price)
                    .build();
      }

      @GetMapping("/{productId}/exists")
      ExistsResponse existsProduct(@PathVariable String productId) {
            return productService.existsProduct(productId);
      }

}