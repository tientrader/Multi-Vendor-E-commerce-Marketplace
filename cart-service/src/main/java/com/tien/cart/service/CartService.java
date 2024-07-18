package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.request.OrderItemCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import com.tien.cart.mapper.CartMapper;
import com.tien.cart.httpclient.ProductClient;
import com.tien.cart.httpclient.OrderClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;
      RedisTemplate<String, Object> redisTemplate;

      // Create a new cart and adds an item to it.
      @Transactional
      public CartResponse createCartAndAddItem(CartCreationRequest cartRequest) {
            ExistsResponse existsResponse = productClient.existsProduct
                    (cartRequest.getProducts().getFirst().getProductId());
            if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

            Cart cart = cartMapper.toCart(cartRequest);
            cart.setId(UUID.randomUUID().toString());

            redisTemplate.opsForValue().set("cart:" + cart.getId(), cart);

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setCartId(cart.getId());

            return cartResponse;
      }

      // Create an order for the given cart.
      @Transactional
      public CartResponse createOrderForCart(String cartId) {
            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            List<OrderItemCreationRequest> orderItems = cart.getProducts().stream()
                    .map(product -> OrderItemCreationRequest.builder()
                            .productId(product.getProductId())
                            .quantity(product.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            OrderCreationRequest orderRequest = OrderCreationRequest.builder()
                    .userId(cart.getUserId())
                    .items(orderItems)
                    .status("CREATED")
                    .build();

            orderClient.createOrder(orderRequest);
            return cartMapper.toCartResponse(cart);
      }

      // Add or update an item in the cart.
      @Transactional
      public CartResponse addOrUpdateItemInCart(String cartId, CartCreationRequest cartRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            ExistsResponse existsResponse = productClient.existsProduct
                    (cartRequest.getProducts().getFirst().getProductId());
            if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

            Cart cart = Optional.ofNullable((Cart) redisTemplate.opsForValue().get("cart:" + cartId))
                    .orElse(Cart.builder()
                            .id(cartId)
                            .userId(userId)
                            .build());
            if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

            cart.setProducts(cartRequest.getProducts());

            redisTemplate.opsForValue().set("cart:" + cartId, cart);

            return cartMapper.toCartResponse(cart);
      }

      // Delete the cart.
      @Transactional
      public void deleteCart(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);
            if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

            redisTemplate.delete("cart:" + cartId);
      }

      // Retrieve the cart by its ID.
      public CartResponse getCartById(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);
            if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

            return cartMapper.toCartResponse(cart);
      }

}