package com.tien.cart.service.impl;

import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.CartItemCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.response.OrderResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.CartItem;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import com.tien.cart.httpclient.OrderClient;
import com.tien.cart.mapper.CartMapper;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.httpclient.ProductClient;
import com.tien.cart.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

      RedisTemplate<String, Object> redisTemplate;
      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;

      private static final String CART_KEY_PREFIX = "cart:";

      @Override
      public CartResponse upsertProductInCart(CartCreationRequest request) {
            String username = getCurrentUsername();
            validateUsername(username);
            String cartKey = CART_KEY_PREFIX + username;

            Cart existingCart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (existingCart == null) {
                  existingCart = createNewCart(request, username);
            } else {
                  updateCartItems(existingCart, request);
            }

            redisTemplate.opsForValue().set(cartKey, existingCart);
            log.info("Cart processed for user: {}", username);
            return cartMapper.toCartResponse(existingCart);
      }

      @Override
      public OrderResponse createOrderFromCart(String paymentMethod, String paymentToken) {
            String username = getCurrentUsername();
            validateUsername(username);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            validateStockAvailability(Objects.requireNonNull(cart).getItems());

            OrderCreationRequest orderRequest = cartMapper.toOrderCreationRequest(cart);
            orderRequest.setEmail(Objects.requireNonNull(cart).getEmail());
            orderRequest.setPaymentMethod(paymentMethod);
            orderRequest.setPaymentToken(paymentToken);

            ApiResponse<OrderResponse> orderResponse = orderClient.createOrder(orderRequest);

            redisTemplate.delete(cartKey);
            log.info("Order created from cart for user: {}", username);

            return orderResponse.getResult();
      }

      @Override
      public void deleteMyCart() {
            String username = getCurrentUsername();
            validateUsername(username);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            redisTemplate.delete(cartKey);
            log.info("Cart deleted for user: {}", username);
      }

      @Override
      public CartResponse getMyCart() {
            String username = getCurrentUsername();
            validateUsername(username);

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            log.info("Fetched cart for user: {}", username);
            return cartMapper.toCartResponse(cart);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Cart createNewCart(CartCreationRequest request, String username) {
            validateProducts(request.getItems());
            double total = calculateTotalPriceForCartItems(request.getItems());

            Cart cart = cartMapper.toCart(request);
            cart.setId(UUID.randomUUID().toString());
            cart.setTotal(total);
            cart.setUsername(username);
            cart.setEmail(request.getEmail());

            log.info("New cart created for user: {}", username);
            return cart;
      }

      private double calculateTotalPriceForCartItems(List<CartItemCreationRequest> cartItems) {
            validateProducts(cartItems);

            return cartItems.stream()
                    .mapToDouble(cartItem -> {
                          Double price = productClient.getProductPriceById(cartItem.getProductId(), cartItem.getVariantId()).getResult();
                          return (price != null ? price : 0.0) * cartItem.getQuantity();
                    }).sum();
      }

      private void updateCartItems(Cart existingCart, CartCreationRequest cartCreationRequest) {
            List<CartItem> updatedItems = existingCart.getItems();

            for (CartItemCreationRequest request : cartCreationRequest.getItems()) {
                  boolean itemFound = false;

                  for (CartItem cartItem : updatedItems) {
                        if (cartItem.getProductId().equals(request.getProductId()) &&
                                cartItem.getVariantId().equals(request.getVariantId())) {

                              itemFound = true;

                              int newQuantity = cartItem.getQuantity() + request.getQuantity();

                              if (newQuantity > 0) {
                                    cartItem.setQuantity(newQuantity);
                                    log.info("Updated item quantity in cart: {} to {}", cartItem.getProductId(), newQuantity);
                              }
                              else {
                                    updatedItems.remove(cartItem);
                                    log.info("Item removed from cart: {}", cartItem.getProductId());
                              }
                              break;
                        }
                  }

                  if (!itemFound && request.getQuantity() > 0) {
                        CartItem newItem = new CartItem();
                        newItem.setProductId(request.getProductId());
                        newItem.setVariantId(request.getVariantId());
                        newItem.setQuantity(request.getQuantity());
                        updatedItems.add(newItem);
                        log.info("Item added to cart: {}", request.getProductId());
                  }
            }

            existingCart.setTotal(calculateTotalPriceForExistingCart(updatedItems));
            log.debug("Updated cart total: {}", existingCart.getTotal());
      }

      private double calculateTotalPriceForExistingCart(List<CartItem> cartItems) {
            return cartItems.stream()
                    .mapToDouble(cartItem -> {
                          Double price = productClient.getProductPriceById(cartItem.getProductId(), cartItem.getVariantId()).getResult();
                          return (price != null ? price : 0.0) * cartItem.getQuantity();
                    }).sum();
      }

      private void validateUsername(String username) {
            if (username == null) {
                  log.error("Unauthorized access attempt");
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
      }

      private void validateCart(Cart cart) {
            if (cart == null) {
                  log.error("Cart not found");
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }
      }

      private void validateProducts(List<CartItemCreationRequest> cartItems) {
            for (CartItemCreationRequest item : cartItems) {
                  String productId = item.getProductId();
                  String variantId = item.getVariantId();

                  ExistsResponse existsResponse = productClient.existsProduct(productId, variantId);
                  if (!existsResponse.isExists()) {
                        log.error("Product or variant not found: productId = {}, variantId = {}", productId, variantId);
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  }
                  log.debug("Product exists: productId = {}, variantId = {}", productId, variantId);
            }
      }

      private void validateStockAvailability(List<CartItem> items) {
            items.forEach(item -> {
                  int stockQuantity = productClient.getProductStockById(
                          item.getProductId(),
                          item.getVariantId()
                  ).getResult();

                  if (stockQuantity < item.getQuantity()) {
                        throw new AppException(ErrorCode.OUT_OF_STOCK);
                  }
            });
      }

}