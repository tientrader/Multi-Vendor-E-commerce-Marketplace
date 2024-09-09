package com.tien.cart.controller;

import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

      CartService cartService;

      @PostMapping("/create")
      ApiResponse<CartResponse> upsertCart(@Valid @RequestBody CartCreationRequest cartRequest) {
            CartResponse cartResponse = cartService.upsertCart(cartRequest);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @PostMapping("/create-order")
      public ApiResponse<Void> createOrderFromCart() {
            cartService.createOrderFromCart();
            return ApiResponse.<Void>builder()
                    .message("Order created successfully")
                    .build();
      }

      @GetMapping
      public ApiResponse<CartResponse> getMyCart() {
            CartResponse cartResponse = cartService.getMyCart();
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @DeleteMapping("/delete")
      public ApiResponse<String> deleteMyCart() {
            cartService.deleteMyCart();
            return ApiResponse.<String>builder()
                    .message("Cart deleted successfully")
                    .build();
      }

}