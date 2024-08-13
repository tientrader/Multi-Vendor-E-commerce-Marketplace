package com.tien.user.controller;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.ShopCreationRequest;
import com.tien.user.dto.request.ShopUpdateRequest;
import com.tien.user.dto.response.ShopResponse;
import com.tien.user.service.ShopService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
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

      @PutMapping
      public ApiResponse<ShopResponse> updateShop(@RequestBody @Valid ShopUpdateRequest request) {
            ShopResponse shopResponse = shopService.updateShop(request);
            return ApiResponse.<ShopResponse>builder()
                    .result(shopResponse)
                    .message("Shop updated successfully")
                    .build();
      }

      @DeleteMapping("/delete")
      public ApiResponse<Void> deleteShop() {
            shopService.deleteShop();
            return ApiResponse.<Void>builder()
                    .message("Shop deleted successfully")
                    .build();
      }

      @GetMapping("/owner/{username}")
      public ApiResponse<ShopResponse> getShopByOwnerUsername(@PathVariable("username") String username) {
            return ApiResponse.<ShopResponse>builder()
                    .result(shopService.getShopByOwnerUsername(username))
                    .build();
      }

}