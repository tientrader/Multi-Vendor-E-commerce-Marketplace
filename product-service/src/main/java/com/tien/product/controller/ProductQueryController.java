package com.tien.product.controller;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.response.ExistsResponse;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.ProductQueryService;
import com.tien.product.service.impl.ProductSort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductQueryController {

      ProductQueryService productQueryService;

      @GetMapping
      ApiResponse<Page<ProductResponse>> getProducts(
              @RequestParam(required = false) String shopId,
              @RequestParam(required = false) String categoryId,
              @RequestParam(defaultValue = "0") int page,
              @RequestParam(defaultValue = "10") int size,
              @RequestParam(defaultValue = "DEFAULT") ProductSort productSort,
              @RequestParam(defaultValue = "asc") String sortDirection,
              @RequestParam(required = false) Double minPrice,
              @RequestParam(required = false) Double maxPrice,
              @RequestParam(defaultValue = "name") String sortBy) {

            Page<ProductResponse> productsPage = productQueryService.getProducts(
                    shopId, categoryId, page, size, sortBy, sortDirection, minPrice, maxPrice, productSort);

            return ApiResponse.<Page<ProductResponse>>builder()
                    .result(productsPage)
                    .build();
      }

      @GetMapping("/{productId}")
      ApiResponse<ProductResponse> getProductById(@PathVariable String productId) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productQueryService.getProductById(productId))
                    .build();
      }

      @GetMapping("/all")
      ApiResponse<List<ProductResponse>> getAllProducts() {
            return ApiResponse.<List<ProductResponse>>builder()
                    .result(productQueryService.getAllProducts())
                    .build();
      }

      @GetMapping("/{productId}/price/{variantId}")
      ApiResponse<Double> getProductPriceById(@PathVariable String productId, @PathVariable String variantId) {
            return ApiResponse.<Double>builder()
                    .result(productQueryService.getProductPriceById(productId, variantId))
                    .build();
      }

      @GetMapping("/{productId}/exists/{variantId}")
      ExistsResponse existsProduct(@PathVariable String productId, @PathVariable String variantId) {
            return productQueryService.existsProduct(productId, variantId);
      }

}