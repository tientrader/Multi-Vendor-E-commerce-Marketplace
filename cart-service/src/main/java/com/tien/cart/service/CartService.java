package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.repository.CartRepository;
import com.tien.cart.mapper.CartMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

      CartRepository cartRepository;
      CartMapper cartMapper;

      public CartResponse createCartAndAddItem(CartCreationRequest cartRequest) {
            Cart cart = cartMapper.toCart(cartRequest);
            cart.setId(UUID.randomUUID().toString());
            Cart savedCart = cartRepository.save(cart);
            return cartMapper.toCartResponse(savedCart);
      }

      public CartResponse addItemToCart(String cartId, CartCreationRequest cartRequest) {
            Cart cart = cartMapper.toCart(cartRequest);
            cart.setId(cartId);
            Cart savedCart = cartRepository.save(cart);
            return cartMapper.toCartResponse(savedCart);
      }

      public Optional<CartResponse> updateCartItem(String cartId, CartCreationRequest cartRequest) {
            Optional<Cart> optionalCart = cartRepository.findById(cartId);
            if (optionalCart.isPresent()) {
                  Cart cart = optionalCart.get();
                  cart.setProductId(cartRequest.getProductId());
                  cart.setQuantity(cartRequest.getQuantity());
                  Cart updatedCart = cartRepository.save(cart);
                  return Optional.of(cartMapper.toCartResponse(updatedCart));
            } else {
                  return Optional.empty();
            }
      }

      public void removeItemFromCart(String cartId) {
            cartRepository.deleteById(cartId);
      }

      public Optional<CartResponse> getCartById(String cartId) {
            Optional<Cart> cart = cartRepository.findById(cartId);
            return cart.map(cartMapper::toCartResponse);
      }

}