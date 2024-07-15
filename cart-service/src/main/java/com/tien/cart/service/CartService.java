package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;
      RedisTemplate<String, Object> redisTemplate;
      Logger logger = LoggerFactory.getLogger(CartService.class);

      @Transactional
      public CartResponse createCartAndAddItem(CartCreationRequest cartRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            logger.info("Checking if product exists: {}", cartRequest.getProducts());
            ExistsResponse existsResponse = productClient.existsProduct(cartRequest.getProducts().getFirst().getProductId());

            if (!existsResponse.isExists()) {
                  throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            Cart cart = cartMapper.toCart(cartRequest);
            cart.setId(UUID.randomUUID().toString());
            cart.setUserId(userId);
            logger.info("Cart created with ID: {}", cart.getId());

            redisTemplate.opsForValue().set("cart:" + cart.getId(), cart);
            logger.info("Cart saved to Redis with ID (create): {}", cart.getId());

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setCartId(cart.getId());
            cartResponse.setUserId(cart.getUserId());

            return cartResponse;
      }

      @Transactional
      public CartResponse createOrderForCart(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Optional<Cart> optionalCart = Optional.ofNullable((Cart) redisTemplate.opsForValue().get("cart:" + cartId));
            if (optionalCart.isPresent()) {
                  Cart cart = optionalCart.get();
                  if (!cart.getUserId().equals(userId)) {
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                  }

                  OrderCreationRequest orderRequest = new OrderCreationRequest(cart.getId(), cart.getProducts().getFirst().getProductId(), cart.getProducts().getFirst().getQuantity());
                  orderClient.createOrder(orderRequest);
                  return cartMapper.toCartResponse(cart);
            } else {
                  throw new IllegalArgumentException("Cart does not exist");
            }
      }

      @Transactional
      public CartResponse addOrUpdateItemInCart(String cartId, CartCreationRequest cartRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            ExistsResponse existsResponse = productClient.existsProduct(cartRequest.getProducts().getFirst().getProductId());
            if (!existsResponse.isExists()) {
                  throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            Cart cart = Optional.ofNullable((Cart) redisTemplate.opsForValue().get("cart:" + cartId))
                    .orElse(Cart.builder().id(cartId).userId(userId).build());

            if (!cart.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            cart.setProducts(cartRequest.getProducts());

            redisTemplate.opsForValue().set("cart:" + cartId, cart);
            logger.info("Cart saved to Redis with ID (update): {}", cartId);

            return cartMapper.toCartResponse(cart);
      }

      public void removeItemFromCart(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) {
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }

            if (!cart.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            redisTemplate.delete("cart:" + cartId);
            logger.info("Cart removed from Redis with ID: {}", cartId);
      }

      public CartResponse getCartById(String cartId) {
            System.out.println("db");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) {
                  logger.error("Cart with ID {} not found in Redis", cartId);
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }

            if (!cart.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            logger.info("Cart fetched from Redis with ID: {}", cartId);
            return cartMapper.toCartResponse(cart);
      }

}