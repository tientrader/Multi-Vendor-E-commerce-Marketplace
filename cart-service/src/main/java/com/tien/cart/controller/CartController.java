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
      ApiResponse<CartResponse> createCartAndAddItem(@Valid @RequestBody CartCreationRequest cartRequest) {
            CartResponse cartResponse = cartService.createCartAndAddItem(cartRequest);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @PostMapping("/{cartId}/add-or-update")
      ApiResponse<CartResponse> addOrUpdateItemInCart(@PathVariable String cartId,
                                                      @Valid @RequestBody CartCreationRequest cartRequest) {
            CartResponse cartResponse = cartService.addOrUpdateItemInCart(cartId, cartRequest);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @PostMapping("/{cartId}/create-order")
      ApiResponse<CartResponse> createOrderForCart(@PathVariable String cartId) {
            CartResponse cartResponse = cartService.createOrderForCart(cartId);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @DeleteMapping("/{cartId}")
      ApiResponse<Void> deleteCart(@PathVariable String cartId) {
            cartService.deleteCart(cartId);
            return ApiResponse.<Void>builder()
                    .message("Cart have been deleted")
                    .build();
      }

      @GetMapping("/{cartId}")
      public ApiResponse<CartResponse> getCartById(@PathVariable String cartId) {
            CartResponse cartResponse = cartService.getCartById(cartId);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

}