package com.tien.cart.service;

import com.tien.cart.dto.request.ProductInCartCreationRequest;
import com.tien.cart.entity.Cart;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
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

      private static final String CART_KEY_PREFIX = "cart:";

      // Create a cart
      public CartResponse createCart(CartCreationRequest cartCreationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            for (ProductInCartCreationRequest item : cartCreationRequest.getProductInCarts()) {
                  String productId = item.getProductId();
                  ExistsResponse existsResponse = productClient.existsProduct(productId);
                  if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            // Fetch product price map
            List<String> productIds = cartCreationRequest.getProductInCarts().stream()
                    .map(ProductInCartCreationRequest::getProductId)
                    .distinct()
                    .toList();

            // Initialize productPriceMap
            Map<String, Double> productPriceMap = productIds.stream()
                    .collect(Collectors.toMap(
                            productId -> productId,
                            productId -> {
                                  ApiResponse<Double> response = productClient.getProductPriceById(productId);
                                  return response.getResult();
                            }
                    ));

            // Calculate total price of the cart
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
            redisTemplate.opsForValue().set(CART_KEY_PREFIX + cart.getId(), cart);

            return cartMapper.toCartResponse(cart);
      }

}