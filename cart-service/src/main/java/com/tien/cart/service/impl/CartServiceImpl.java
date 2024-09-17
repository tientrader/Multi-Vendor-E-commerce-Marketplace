package com.tien.cart.service.impl;

import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.request.ProductInCartCreationRequest;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.ProductInCart;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import com.tien.cart.httpclient.OrderClient;
import com.tien.cart.mapper.CartMapper;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.httpclient.ProductClient;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

      RedisTemplate<String, Object> redisTemplate;
      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;

      private static final String CART_KEY_PREFIX = "cart:";

      public CartResponse upsertCart(CartCreationRequest request) {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;
            Cart existingCart = (Cart) redisTemplate.opsForValue().get(cartKey);

            if (existingCart != null) {
                  updateCart(existingCart, request);
                  redisTemplate.opsForValue().set(cartKey, existingCart);
                  return cartMapper.toCartResponse(existingCart);
            }

            validateProducts(request.getProductInCarts());
            double total = calculateTotal(request.getProductInCarts());

            Cart cart = cartMapper.toCart(request);
            cart.setId(UUID.randomUUID().toString());
            cart.setTotal(total);
            cart.setUsername(username);
            cart.setEmail(request.getEmail());

            redisTemplate.opsForValue().set(cartKey, cart);
            return cartMapper.toCartResponse(cart);
      }

      public void createOrderFromCart() {
            String username = getCurrentUsername();
            if (username == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            OrderCreationRequest orderRequest = cartMapper.toOrderCreationRequest(cart);
            orderRequest.setStatus("PENDING");
            orderRequest.setEmail(cart.getEmail());

            orderClient.createOrder(orderRequest);
            redisTemplate.delete(cartKey);
      }

      public CartResponse getMyCart() {
            String username = getCurrentUsername();
            if (username == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            return cartMapper.toCartResponse(cart);
      }

      public void deleteMyCart() {
            String username = getCurrentUsername();
            if (username == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            redisTemplate.delete(cartKey);
      }

      private void updateCart(Cart existingCart, CartCreationRequest cartCreationRequest) {
            List<ProductInCart> productInCarts = cartCreationRequest.getProductInCarts().stream()
                    .map(request -> ProductInCart.builder()
                            .productId(request.getProductId())
                            .quantity(request.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            existingCart.setProductInCarts(productInCarts);

            double total = productInCarts.stream()
                    .mapToDouble(productInCart -> {
                          Double price = productClient.getProductPriceById(productInCart.getProductId()).getResult();
                          if (price == null) price = 0.0;
                          return price * productInCart.getQuantity();
                    }).sum();

            existingCart.setTotal(total);
      }

      private void validateProducts(List<ProductInCartCreationRequest> productInCarts) {
            for (ProductInCartCreationRequest item : productInCarts) {
                  String productId = item.getProductId();
                  ExistsResponse existsResponse = productClient.existsProduct(productId);
                  if (!existsResponse.isExists()) {
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  }
            }
      }

      private double calculateTotal(List<ProductInCartCreationRequest> productInCarts) {
            List<String> productIds = productInCarts.stream()
                    .map(ProductInCartCreationRequest::getProductId)
                    .distinct()
                    .toList();

            Map<String, Double> productPriceMap = productIds.stream()
                    .collect(Collectors.toMap(
                            productId -> productId,
                            productId -> {
                                  ApiResponse<Double> response = productClient.getProductPriceById(productId);
                                  return response.getResult();
                            }
                    ));

            return productInCarts.stream()
                    .mapToDouble(productInCart -> {
                          Double price = productPriceMap.get(productInCart.getProductId());
                          if (price == null) price = 0.0;
                          return price * productInCart.getQuantity();
                    }).sum();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

}