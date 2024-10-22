package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.dto.response.OrderResponse;

public interface CartService {

      CartResponse upsertProductInCart(CartCreationRequest request);

      OrderResponse createOrderFromCart(String paymentMethod, String paymentToken);

      void deleteMyCart();

      CartResponse getMyCart();

}