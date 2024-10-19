package com.tien.product.controller;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.ProductService;
import com.tien.product.service.impl.ProductSort;
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
      public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.createProduct(request))
                    .build();
      }

      @PutMapping("/{productId}")
      public ApiResponse<ProductResponse> updateProduct(@PathVariable String productId,
                                                 @RequestBody @Valid ProductUpdateRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(productId, request))
                    .build();
      }

      @PutMapping("/{productId}/update-stock-sold")
      public ApiResponse<Void> updateStockAndSoldQuantity(@PathVariable String productId,
                                                   @RequestParam String variantId,
                                                   @RequestParam int quantity) {
            productService.updateStockAndSoldQuantity(productId, variantId, quantity);
            return ApiResponse.<Void>builder()
                    .message("Variant stock and sold quantity updated successfully")
                    .build();
      }

      @DeleteMapping("/{productId}")
      public ApiResponse<String> deleteProduct(@PathVariable String productId) {
            productService.deleteProduct(productId);
            return ApiResponse.<String>builder()
                    .result("Product has been deleted")
                    .build();
      }

      @GetMapping
      public ApiResponse<Page<ProductResponse>> getProducts(
              @RequestParam(required = false) String shopId,
              @RequestParam(required = false) String categoryId,
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "DEFAULT") ProductSort productSort,
              @RequestParam(defaultValue = "asc") String sortDirection,
              @RequestParam(required = false) Double minPrice,
              @RequestParam(required = false) Double maxPrice,
              @RequestParam(defaultValue = "name") String sortBy) {

            Page<ProductResponse> productsPage = productService.getProducts(
                    shopId, categoryId, page, size, sortBy, sortDirection, minPrice, maxPrice, productSort);

            return ApiResponse.<Page<ProductResponse>>builder()
                    .result(productsPage)
                    .build();
      }

      @GetMapping("/{productId}")
      public ApiResponse<ProductResponse> getProductById(@PathVariable String productId) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.getProductById(productId))
                    .build();
      }

      @GetMapping("/{productId}/stock/{variantId}")
      public ApiResponse<Integer> getProductStockById(@PathVariable String productId, @PathVariable String variantId) {
            return ApiResponse.<Integer>builder()
                    .result(productService.getProductStockById(productId, variantId))
                    .build();
      }

      @GetMapping("/all")
      public ApiResponse<List<ProductResponse>> getAllProducts() {
            return ApiResponse.<List<ProductResponse>>builder()
                    .result(productService.getAllProducts())
                    .build();
      }

      @GetMapping("/{productId}/price/{variantId}")
      public ApiResponse<Double> getProductPriceById(@PathVariable String productId, @PathVariable String variantId) {
            return ApiResponse.<Double>builder()
                    .result(productService.getProductPriceById(productId, variantId))
                    .build();
      }

      @GetMapping("/{productId}/exists/{variantId}")
      public ExistsResponse existsProduct(@PathVariable String productId, @PathVariable String variantId) {
            return productService.existsProduct(productId, variantId);
      }

}