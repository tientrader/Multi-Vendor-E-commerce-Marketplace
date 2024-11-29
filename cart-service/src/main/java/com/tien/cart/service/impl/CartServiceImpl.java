package com.tien.cart.service.impl;

import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.CartItemCreationRequest;
import com.tien.cart.httpclient.request.OrderCreationRequest;
import com.tien.cart.dto.response.CartItemResponse;
import com.tien.cart.httpclient.response.OrderResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.CartItem;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import com.tien.cart.httpclient.OrderClient;
import com.tien.cart.httpclient.PromotionClient;
import com.tien.cart.httpclient.ShopClient;
import com.tien.cart.mapper.CartMapper;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.httpclient.response.ExistsResponse;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.httpclient.ProductClient;
import com.tien.cart.service.CartService;
import com.tien.cart.service.RedisService;
import feign.FeignException;
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
      RedisService redisService;
      CartMapper cartMapper;
      ProductClient productClient;
      OrderClient orderClient;
      ShopClient shopClient;
      PromotionClient promotionClient;

      private static final String CART_KEY_PREFIX = "cart:";
      private static final String PROMO_APPLIED_KEY_PREFIX = "promoApplied:";

      @Override
      public CartResponse upsertProductInCart(CartCreationRequest request) {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;

            validateShopOwnership(request);

            Cart existingCart = (Cart) redisTemplate.opsForValue().get(cartKey);
            if (existingCart == null) {
                  existingCart = createNewCart(request, username);
            } else {
                  updateCartItems(existingCart, request);
                  Boolean promoApplied = (Boolean) redisTemplate.opsForValue().get(PROMO_APPLIED_KEY_PREFIX + username);
                  if (promoApplied != null && promoApplied) {
                        redisTemplate.delete(PROMO_APPLIED_KEY_PREFIX + username);
                        existingCart.setDiscountApplied(false);
                  }
            }

            List<CartItemResponse> itemResponses = existingCart.getItems()
                    .stream()
                    .map(cartItem -> {
                          double itemTotalPrice = calculateItemTotalPrice(cartItem);
                          CartItemResponse response = cartMapper.toCartItemResponse(cartItem);
                          response.setTotalPrice(itemTotalPrice);
                          cartItem.setTotalPrice(itemTotalPrice);
                          return response;
                    })
                    .toList();

            CartResponse cartResponse = cartMapper.toCartResponse(existingCart);
            cartResponse.setItems(itemResponses);

            double total = itemResponses.stream()
                    .mapToDouble(CartItemResponse::getTotalPrice)
                    .sum();
            cartResponse.setTotal(total);

            redisService.saveWithDefaultTTL(cartKey, existingCart);
            return cartResponse;
      }

      @Override
      public void applyPromotionCodeToCart(String promoCode) {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            if (Objects.requireNonNull(cart).getItems() == null || cart.getItems().isEmpty()) {
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }

            redisTemplate.opsForValue().set(PROMO_APPLIED_KEY_PREFIX + username, true);

            try {
                  promotionClient.applyPromotionCode(promoCode);
            } catch (FeignException e) {
                  if (e.status() == 400 && e.contentUTF8().contains(ErrorCode.ORDER_VALUE_TOO_LOW.name())) {
                        log.error("PromotionService: Order value too low for promoCode {}", promoCode);
                        throw new AppException(ErrorCode.ORDER_VALUE_TOO_LOW);
                  }
                  log.error("Failed to apply promotion code: {}. Status: {}", promoCode, e.status());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

      @Override
      public OrderResponse createOrderFromCart(String paymentMethod, String paymentToken) {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            validateStockAvailability(Objects.requireNonNull(cart).getItems());

            OrderCreationRequest orderRequest = cartMapper.toOrderCreationRequest(cart);
            orderRequest.setEmail(Objects.requireNonNull(cart).getEmail());
            orderRequest.setPaymentMethod(paymentMethod);
            orderRequest.setPaymentToken(paymentToken);

            ApiResponse<OrderResponse> orderResponse;
            try {
                  orderResponse = orderClient.createOrder(orderRequest);
            } catch (FeignException e) {
                  log.error("FeignException occurred while creating order for user {}: {}",
                          username, e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            redisTemplate.delete(cartKey);
            return orderResponse.getResult();
      }

      @Override
      public void updateCartTotal(String username, double total) {
            Boolean promoApplied = (Boolean) redisTemplate.opsForValue().get(PROMO_APPLIED_KEY_PREFIX + username);

            if (promoApplied == null || !promoApplied) {
                  log.error("User {} attempted to directly update cart total without applying promotion", username);
                  throw new AppException(ErrorCode.INVALID_OPERATION);
            }

            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);

            if (cart == null) {
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }

            if (cart.isDiscountApplied()) {
                  log.error("Discount already applied for cart of user {}", username);
                  throw new AppException(ErrorCode.DISCOUNT_ALREADY_APPLIED);
            }

            cart.setTotal(total);
            cart.setDiscountApplied(true);

            redisService.saveWithDefaultTTL(cartKey, cart);
      }

      @Override
      public void deleteMyCart() {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

            redisTemplate.delete(cartKey);
      }

      @Override
      public CartResponse getMyCart() {
            String username = getCurrentUsername();
            String cartKey = CART_KEY_PREFIX + username;
            Cart cart = (Cart) redisTemplate.opsForValue().get(cartKey);
            validateCart(cart);

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

            return cart;
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
                              }
                              else {
                                    updatedItems.remove(cartItem);
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
                  }
            }

            existingCart.setTotal(calculateTotalPriceForExistingCart(updatedItems));
      }

      private void validateCart(Cart cart) {
            if (cart == null) {
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }
      }

      private double calculateItemTotalPrice(CartItem cartItem) {
            try {
                  Double price = productClient.getProductPriceById(cartItem.getProductId(), cartItem.getVariantId()).getResult();
                  return (price != null ? price : 0.0) * cartItem.getQuantity();
            } catch (FeignException e) {
                  log.error("(TotalPrice) FeignException occurred while fetching price for productId={}, variantId={}: {}",
                          cartItem.getProductId(), cartItem.getVariantId(), e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

      private double calculateTotalPriceForCartItems(List<CartItemCreationRequest> cartItems) {
            validateProducts(cartItems);

            return cartItems.stream()
                    .mapToDouble(cartItem -> {
                          try {
                                Double price = productClient.getProductPriceById(cartItem.getProductId(), cartItem.getVariantId()).getResult();
                                return (price != null ? price : 0.0) * cartItem.getQuantity();
                          } catch (FeignException e) {
                                log.error("FeignException occurred while fetching price for productId={}, variantId={}: {}",
                                        cartItem.getProductId(), cartItem.getVariantId(), e.getMessage());
                                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                          }
                    }).sum();
      }

      private double calculateTotalPriceForExistingCart(List<CartItem> cartItems) {
            return cartItems.stream()
                    .mapToDouble(cartItem -> {
                          try {
                                Double price = productClient.getProductPriceById(cartItem.getProductId(), cartItem.getVariantId()).getResult();
                                return (price != null ? price : 0.0) * cartItem.getQuantity();
                          } catch (FeignException e) {
                                log.error("(ExistingCart) FeignException occurred while fetching price for productId={}, variantId={}: {}",
                                        cartItem.getProductId(), cartItem.getVariantId(), e.getMessage());
                                throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                          }
                    }).sum();
      }

      private void validateShopOwnership(CartCreationRequest request) {
            for (CartItemCreationRequest item : request.getItems()) {
                  try {
                        String shopId = productClient.getShopIdByProductId(item.getProductId()).getResult();
                        String ownerUsername = shopClient.getOwnerUsernameByShopId(shopId).getResult();

                        if (ownerUsername != null && ownerUsername.equals(getCurrentUsername())) {
                              log.error("User {} attempted to add their own product to the cart: productId={}", getCurrentUsername(), item.getProductId());
                              throw new AppException(ErrorCode.CANNOT_ADD_OWN_PRODUCT);
                        }
                  } catch (FeignException e) {
                        log.error("FeignException occurred while checking shop ownership for productId={}: {}", item.getProductId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            }
      }

      private void validateProducts(List<CartItemCreationRequest> cartItems) {
            for (CartItemCreationRequest item : cartItems) {
                  try {
                        ExistsResponse existsResponse = productClient.existsProduct(item.getProductId(), item.getVariantId()).getResult();
                        if (!existsResponse.isExists()) {
                              throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                        }
                  } catch (FeignException e) {
                        log.error("FeignException occurred while checking product existence for productId={}, variantId={}: {}",
                                item.getProductId(), item.getVariantId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            }
      }

      private void validateStockAvailability(List<CartItem> items) {
            items.forEach(item -> {
                  try {
                        int stockQuantity = productClient.getProductStockById(item.getProductId(), item.getVariantId()).getResult();
                        if (stockQuantity < item.getQuantity()) {
                              log.error("Out of stock: productId={}, variantId={}, requestedQuantity={}, availableQuantity={}", item.getProductId(), item.getVariantId(), item.getQuantity(), stockQuantity);
                              throw new AppException(ErrorCode.OUT_OF_STOCK);
                        }
                  } catch (FeignException e) {
                        log.error("FeignException occurred while checking stock for productId={}, variantId={}: {}",
                                item.getProductId(), item.getVariantId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            });
      }

}