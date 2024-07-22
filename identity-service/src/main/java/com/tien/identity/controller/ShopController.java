package com.tien.identity.controller;

import com.tien.identity.dto.ApiResponse;
import com.tien.identity.dto.request.ShopCreationRequest;
import com.tien.identity.dto.response.ShopResponse;
import com.tien.identity.service.ShopService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopController {

      ShopService shopService;

      @PostMapping("/create")
      public ApiResponse<ShopResponse> createShop(@Valid @RequestBody ShopCreationRequest request) {
            ShopResponse shopResponse = shopService.createShop(request);
            return ApiResponse.<ShopResponse>builder()
                    .result(shopResponse)
                    .message("Shop created successfully")
                    .build();
      }

}