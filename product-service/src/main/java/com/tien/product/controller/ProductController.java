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

      @PostMapping("/create")
      ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.createProduct(request))
                    .build();
      }

      @GetMapping
      public ApiResponse<Page<ProductResponse>> getHomepageProductList(
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "name") String sortBy,
              @RequestParam(defaultValue = "asc") String sortDirection,
              @RequestParam(required = false) String categoryId,
              @RequestParam(required = false) Double minPrice,
              @RequestParam(required = false) Double maxPrice) {
            Page<ProductResponse> productsPage = productService.getHomepageProductList(
                    page, size, sortBy, sortDirection, categoryId, minPrice, maxPrice);

            return ApiResponse.<Page<ProductResponse>>builder()
                    .result(productsPage)
                    .build();
      }

      @GetMapping("/shop/{shopId}")
      public ApiResponse<Page<ProductResponse>> getProductsByShop(
              @PathVariable String shopId,
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "name") String sortBy,
              @RequestParam(defaultValue = "asc") String sortDirection,
              @RequestParam(required = false) String categoryId,
              @RequestParam(required = false) Double minPrice,
              @RequestParam(required = false) Double maxPrice) {

            Page<ProductResponse> productsPage = productService.getProductsByShop(
                    shopId, page, size, sortBy, sortDirection, categoryId, minPrice, maxPrice);

            return ApiResponse.<Page<ProductResponse>>builder()
                    .result(productsPage)
                    .build();
      }

      @GetMapping("/categoryInShop/{categoryId}")
      public ApiResponse<Page<ProductResponse>> getProductsByCategoryId(
              @PathVariable String categoryId,
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "name") String sortBy,
              @RequestParam(defaultValue = "asc") String sortDirection,
              @RequestParam(required = false) Double minPrice,
              @RequestParam(required = false) Double maxPrice) {

            Page<ProductResponse> productsPage = productService.getProductsByCategoryId(
                    categoryId, page, size, sortBy, sortDirection, minPrice, maxPrice);

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
            return ApiResponse.<Double>builder()
                    .result(productService.getProductPriceById(productId))
                    .build();
      }

      @GetMapping("/{productId}/exists")
      ExistsResponse existsProduct(@PathVariable String productId) {
            return productService.existsProduct(productId);
      }

      @PutMapping("/{productId}")
      ApiResponse<ProductResponse> updateProduct(@PathVariable String productId,
                                                 @RequestBody @Valid ProductUpdateRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(productId, request))
                    .build();
      }

      @PutMapping("/{productId}/update-stock-sold")
      ApiResponse<Void> updateStockAndSoldQuantity(@PathVariable String productId, @RequestParam int quantity) {
            productService.updateStockAndSoldQuantity(productId, quantity);
            return ApiResponse.<Void>builder()
                    .message("Stock and sold quantity updated successfully")
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