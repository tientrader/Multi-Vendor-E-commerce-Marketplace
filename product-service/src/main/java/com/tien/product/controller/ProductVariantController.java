package com.tien.product.controller;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.ProductVariantRequest;
import com.tien.product.dto.request.ProductVariantUpdateRequest;
import com.tien.product.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/variants")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantController {

      ProductVariantService productVariantService;

      @PostMapping("/{productId}")
      public ApiResponse<Void> addProductVariant(@PathVariable String productId,
                                                 @RequestBody @Valid ProductVariantRequest request) {
            productVariantService.addProductVariant(productId, request);
            return ApiResponse.<Void>builder()
                    .message("Product variant added successfully")
                    .build();
      }

      @PutMapping("/{productId}/{variantId}")
      public ApiResponse<Void> updateProductVariant(@PathVariable String productId,
                                                    @PathVariable String variantId,
                                                    @RequestBody @Valid ProductVariantUpdateRequest request) {
            productVariantService.updateProductVariant(productId, variantId, request);
            return ApiResponse.<Void>builder()
                    .message("Product variant updated successfully")
                    .build();
      }

      @PutMapping("/{productId}/update-stock-sold")
      public ApiResponse<Void> updateStockAndSoldQuantity(@PathVariable String productId,
                                                          @RequestParam String variantId,
                                                          @RequestParam int quantity) {
            productVariantService.updateStockAndSoldQuantity(productId, variantId, quantity);
            return ApiResponse.<Void>builder()
                    .message("Variant stock and sold quantity updated successfully")
                    .build();
      }

      @DeleteMapping("/{productId}/{variantId}")
      public ApiResponse<String> deleteProductVariant(@PathVariable String productId,
                                                      @PathVariable String variantId) {
            productVariantService.deleteProductVariant(productId, variantId);
            return ApiResponse.<String>builder()
                    .result("Product variant has been deleted")
                    .build();
      }

}