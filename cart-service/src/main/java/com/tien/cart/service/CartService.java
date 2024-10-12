package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;

public interface CartService {

      CartResponse upsertProductInCart(CartCreationRequest request);
      void createOrderFromCart();
      void deleteMyCart();
      CartResponse getMyCart();

}