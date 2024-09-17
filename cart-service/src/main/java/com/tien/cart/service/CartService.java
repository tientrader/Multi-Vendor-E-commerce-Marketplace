package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;

public interface CartService {

      CartResponse upsertCart(CartCreationRequest request);
      void createOrderFromCart();
      CartResponse getMyCart();
      void deleteMyCart();

}