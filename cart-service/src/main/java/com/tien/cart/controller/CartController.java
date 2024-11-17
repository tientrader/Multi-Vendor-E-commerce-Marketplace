package com.tien.cart.controller;

import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.dto.response.OrderResponse;
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
      public ApiResponse<CartResponse> upsertProductInCart(@Valid @RequestBody CartCreationRequest cartRequest) {
            return ApiResponse.<CartResponse>builder()
                    .result(cartService.upsertProductInCart(cartRequest))
                    .build();
      }

      @PostMapping("/create-order")
      public ApiResponse<OrderResponse> createOrderFromCart(
              @RequestParam("paymentMethod") String paymentMethod,
              @RequestParam(value = "paymentToken", required = false) String paymentToken) {
            return ApiResponse.<OrderResponse>builder()
                    .result(cartService.createOrderFromCart(paymentMethod, paymentToken))
                    .build();
      }

      @DeleteMapping("/delete")
      public ApiResponse<String> deleteMyCart() {
            cartService.deleteMyCart();
            return ApiResponse.<String>builder()
                    .message("Cart deleted successfully")
                    .build();
      }

      @GetMapping
      public ApiResponse<CartResponse> getMyCart() {
            return ApiResponse.<CartResponse>builder()
                    .result(cartService.getMyCart())
                    .build();
      }

}