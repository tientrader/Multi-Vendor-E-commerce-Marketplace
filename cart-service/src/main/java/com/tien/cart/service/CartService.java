package com.tien.cart.service;

import com.tien.cart.dto.request.ProductInCartCreationRequest;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import com.tien.cart.mapper.CartMapper;
import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.response.ProductResponse;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.httpclient.ProductClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
            // Validate if products exist
            for (ProductInCartCreationRequest item : cartCreationRequest.getProductInCarts()) {
                  String productId = item.getProductId();
                  log.debug("Checking if product with ID {} exists.", productId);
                  ExistsResponse existsResponse = productClient.existsProduct(productId);
                  log.debug("Received ExistsResponse for product ID {}: {}", productId, existsResponse);
                  if (!existsResponse.isExists()) {
                        log.error("Product with ID {} does not exist", productId);
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  }
                  log.info("Product with ID {} exists.", productId);
            }

            // Fetch product price map
            log.info("Fetching product price map from ProductService");

            List<String> productIds = cartCreationRequest.getProductInCarts().stream()
                    .map(ProductInCartCreationRequest::getProductId).distinct().toList();

            Map<String, ProductResponse> productResponseMap = productIds.stream()
                    .collect(Collectors.toMap(productId -> productId, productClient::getProductById));

            Map<String, Double> productPriceMap = productResponseMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPrice()));

            log.info("Product price map fetched successfully: {}", productPriceMap);

            // Calculate total price of the cart
            double total = cartCreationRequest.getProductInCarts().stream()
                    .mapToDouble(productInCart -> {
                          Double price = productPriceMap.get(productInCart.getProductId());
                          if (price == null) price = 0.0;
                          return price * productInCart.getQuantity();
                    }).sum();
            log.info("Total price calculated: {}", total);

            Cart cart = cartMapper.toCart(cartCreationRequest);
            cart.setId(UUID.randomUUID().toString());
            cart.setTotal(total);

            redisTemplate.opsForValue().set(CART_KEY_PREFIX + cart.getId(), cart);
            log.info("Saving cart to Redis with ID: {}", cart.getId());

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            log.info("Cart created successfully: {}", cartResponse);

            return cartResponse;
      }

}