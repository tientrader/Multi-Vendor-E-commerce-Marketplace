package com.tien.shop.controller;

import com.tien.shop.dto.ApiResponse;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ShopResponse;
import com.tien.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopController {

      ShopService shopService;

      @PostMapping("/create")
      public ApiResponse<ShopResponse> createShop(@Valid @RequestBody ShopCreationRequest request) {
            return ApiResponse.<ShopResponse>builder()
                    .result(shopService.createShop(request))
                    .build();
      }

      @PutMapping("/update")
      public ApiResponse<ShopResponse> updateShop(@RequestBody @Valid ShopUpdateRequest request) {
            return ApiResponse.<ShopResponse>builder()
                    .result(shopService.updateShop(request))
                    .build();
      }

      @DeleteMapping("/delete")
      public ApiResponse<Void> deleteShop() {
            shopService.deleteShop();
            return ApiResponse.<Void>builder()
                    .message("Shop deleted successfully")
                    .build();
      }

      @GetMapping("/{shopId}/revenue")
      public ApiResponse<Double> getRevenueByShopId(@PathVariable("shopId") String shopId) {
            return ApiResponse.<Double>builder()
                    .result(shopService.calculateRevenueForShop(shopId))
                    .build();
      }

      @GetMapping("/owner/{username}")
      public ApiResponse<ShopResponse> getShopByOwnerUsername(@PathVariable("username") String username) {
            return ApiResponse.<ShopResponse>builder()
                    .result(shopService.getShopByOwnerUsername(username))
                    .build();
      }

      @GetMapping("/{shopId}/owner")
      public ApiResponse<String> getOwnerUsernameByShopId(@PathVariable("shopId") String shopId) {
            String ownerUsername = shopService.getOwnerUsernameByShopId(shopId);
            return ApiResponse.<String>builder()
                    .result(ownerUsername)
                    .build();
      }

}