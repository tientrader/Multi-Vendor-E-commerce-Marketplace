package com.tien.product.controller;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.service.ProductCommandService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductCommandController {

      ProductCommandService productCommandService;

      @PostMapping("/create")
      ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productCommandService.createProduct(request))
                    .build();
      }

      @PutMapping("/{productId}")
      ApiResponse<ProductResponse> updateProduct(@PathVariable String productId,
                                                 @RequestBody @Valid ProductUpdateRequest request) {
            return ApiResponse.<ProductResponse>builder()
                    .result(productCommandService.updateProduct(productId, request))
                    .build();
      }

      @PutMapping("/{productId}/update-stock-sold")
      ApiResponse<Void> updateStockAndSoldQuantity(@PathVariable String productId,
                                                   @RequestParam int quantity) {
            productCommandService.updateStockAndSoldQuantity(productId, quantity);
            return ApiResponse.<Void>builder()
                    .message("Stock and sold quantity updated successfully")
                    .build();
      }

      @DeleteMapping("/{productId}")
      ApiResponse<String> deleteProduct(@PathVariable String productId) {
            productCommandService.deleteProduct(productId);
            return ApiResponse.<String>builder()
                    .result("Product has been deleted")
                    .build();
      }

}