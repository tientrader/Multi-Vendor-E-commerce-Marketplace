package com.tien.cart.service;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

      RedisTemplate<String, Object> redisTemplate;
      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;

      private static final String CART_KEY_PREFIX = "cart:";

      // Create new cart or update (if cart already exists)
      public CartResponse createCart(CartCreationRequest cartCreationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            String existingCartKey = CART_KEY_PREFIX + userId;
            Cart existingCart = (Cart) redisTemplate.opsForValue().get(existingCartKey);

            if (existingCart != null) {
                  updateCart(existingCart, cartCreationRequest);
                  redisTemplate.opsForValue().set(existingCartKey, existingCart);
                  return cartMapper.toCartResponse(existingCart);
            }

            for (ProductInCartCreationRequest item : cartCreationRequest.getProductInCarts()) {
                  String productId = item.getProductId();
                  ExistsResponse existsResponse = productClient.existsProduct(productId);
                  if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            List<String> productIds = cartCreationRequest.getProductInCarts().stream()
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

            double total = cartCreationRequest.getProductInCarts().stream()
                    .mapToDouble(productInCart -> {
                          Double price = productPriceMap.get(productInCart.getProductId());
                          if (price == null) price = 0.0;
                          return price * productInCart.getQuantity();
                    }).sum();

            Cart cart = cartMapper.toCart(cartCreationRequest);
            cart.setId(UUID.randomUUID().toString());
            cart.setTotal(total);
            cart.setUserId(userId);

            redisTemplate.opsForValue().set(CART_KEY_PREFIX + userId, cart);

            return cartMapper.toCartResponse(cart);
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
                          Double price = productClient.getProductPriceById
                                  (productInCart.getProductId()).getResult();
                          if (price == null) price = 0.0;
                          return price * productInCart.getQuantity();
                    }).sum();

            existingCart.setTotal(total);
      }

      // Create order from cart
      public void createOrderFromCart() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            if (userId == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + userId;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            OrderCreationRequest orderRequest = cartMapper.toOrderCreationRequest(cart);
            orderRequest.setStatus("PENDING");

            orderClient.createOrder(orderRequest);

            redisTemplate.delete(cartKey);
      }

      // Delete the user's cart
      public void deleteCart() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            if (userId == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + userId;

            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            redisTemplate.delete(cartKey);
      }

      // Get the user's cart
      public CartResponse getCart() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            if (userId == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            String cartKey = CART_KEY_PREFIX + userId;
            
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);

            return cartMapper.toCartResponse(cart);
      }

}