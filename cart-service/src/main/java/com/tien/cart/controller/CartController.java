package com.tien.cart.controller;

import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

      CartService cartService;

      @PostMapping("/create")
      ApiResponse<CartResponse> createCartAndAddItem(@Validated @RequestBody CartCreationRequest cartRequest) {
            CartResponse cartResponse = cartService.createCartAndAddItem(cartRequest);
            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @PostMapping("/{cartId}")
      ApiResponse<CartResponse> addItemToCart(@PathVariable String cartId,
                                              @Validated @RequestBody CartCreationRequest cartRequest) {
            CartResponse cartResponse;
            Optional<CartResponse> existingCart = cartService.getCartById(cartId);

            if (existingCart.isPresent()) {
                  cartResponse = cartService.addItemToCart(cartId, cartRequest);
            } else {
                  cartResponse = cartService.createCartAndAddItem(cartRequest);
            }

            return ApiResponse.<CartResponse>builder()
                    .result(cartResponse)
                    .build();
      }

      @PutMapping("/{cartId}")
      ApiResponse<CartResponse> updateCartItem(@PathVariable String cartId,
                                               @Validated @RequestBody CartCreationRequest cartRequest) {
            Optional<CartResponse> cartResponse = cartService.updateCartItem(cartId, cartRequest);
            return cartResponse.map(responseData ->
                            ApiResponse.<CartResponse>builder()
                                    .code(200)
                                    .message("Cart item updated")
                                    .result(responseData)
                                    .build())
                    .orElseGet(() ->
                            ApiResponse.<CartResponse>builder()
                                    .code(404)
                                    .message("Cart not found")
                                    .build());
      }

      @DeleteMapping("/{cartId}")
      ApiResponse<Void> removeItemFromCart(@PathVariable String cartId) {
            cartService.removeItemFromCart(cartId);
            return ApiResponse.<Void>builder()
                    .message("Item removed from cart")
                    .build();
      }

      @GetMapping("/{cartId}")
      ApiResponse<CartResponse> getCartById(@PathVariable String cartId) {
            Optional<CartResponse> cartResponse = cartService.getCartById(cartId);
            return cartResponse.map(responseData ->
                            ApiResponse.<CartResponse>builder()
                                    .code(200)
                                    .message("Cart retrieved")
                                    .result(responseData)
                                    .build())
                    .orElseGet(() ->
                            ApiResponse.<CartResponse>builder()
                                    .code(404)
                                    .message("Cart not found")
                                    .build());
      }

}