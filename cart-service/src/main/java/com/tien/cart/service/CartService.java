package com.tien.cart.service;

import com.tien.cart.dto.request.CartCreationRequest;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.dto.request.OrderItemCreationRequest;
import com.tien.cart.dto.response.CartResponse;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.dto.response.ProductResponse;
import com.tien.cart.entity.Cart;
import com.tien.cart.entity.ProductInCart;
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

import java.util.List;
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
      Logger logger = LoggerFactory.getLogger(CartService.class);

      @Transactional
      public CartResponse createCartAndAddItem(CartCreationRequest cartRequest) {
            String userId = cartRequest.getUserId();
            logger.info("Creating a new cart for user ID: {}", userId);

            for (ProductInCart product : cartRequest.getProducts()) {
                  String productId = product.getProductId();
                  logger.debug("Checking if product with ID {} exists.", productId);
                  ExistsResponse existsResponse = productClient.existsProduct(productId);
                  if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                  logger.info("Product with ID {} exists.", productId);
            }

            logger.info("Fetching product details for products in the cart.");
            List<ProductResponse> productResponses = cartRequest.getProducts().stream()
                    .map(product -> {
                          String productId = product.getProductId();
                          logger.debug("Fetching details for product ID {}.", productId);
                          ProductResponse productResponse = productClient.getProductById(productId);
                          if (productResponse == null) {
                                logger.error("Product details for ID {} could not be fetched.", productId);
                                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                          }
                          logger.info("Product details for ID {} fetched successfully.", productId);
                          return productResponse;
                    }).toList();

            Cart cart = cartMapper.toCart(cartRequest);
            cart.setId(UUID.randomUUID().toString());
            logger.info("Cart ID set to {}", cart.getId());

            logger.info("Calculating total value of the cart.");
            double total = calculateTotal(cartRequest.getProducts(), productResponses);
            logger.info("Total value calculated: {}", total);
            cart.setTotal(total);

            logger.info("Saving cart with ID {} to Redis.", cart.getId());
            redisTemplate.opsForValue().set("cart:" + cart.getId(), cart);

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setCartId(cart.getId());
            cartResponse.setTotal(total);

            logger.info("Cart created successfully with ID: {}", cart.getId());
            logger.debug("Cart details: {}", cartResponse);
            return cartResponse;
      }

      private double calculateTotal(List<ProductInCart> products, List<ProductResponse> productResponses) {
            return products.stream()
                    .mapToDouble(productInCart -> {
                          ProductResponse productResponse = productResponses.stream()
                                  .filter(response -> response.getId().equals(productInCart.getProductId()))
                                  .findFirst()
                                  .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                          return productResponse.getPrice() * productInCart.getQuantity();
                    }).sum();
      }

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
                    .total(cart.getTotal())
                    .status("CREATED")
                    .build();

            orderClient.createOrder(orderRequest);
            return cartMapper.toCartResponse(cart);
      }

      @Transactional
      public CartResponse addOrUpdateItemInCart(String cartId, CartCreationRequest cartRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            for (ProductInCart product : cartRequest.getProducts()) {
                  ExistsResponse existsResponse = productClient.existsProduct(product.getProductId());
                  if (!existsResponse.isExists()) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) {
                  cart = Cart.builder()
                          .id(cartId)
                          .userId(userId)
                          .build();
            } else if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);


            cart.setProducts(cartRequest.getProducts());

            List<ProductResponse> productResponses = cartRequest.getProducts().stream()
                    .map(product -> {
                          ProductResponse productResponse = productClient.getProductById(product.getProductId());
                          if (productResponse == null) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                          return productResponse;
                    }).collect(Collectors.toList());

            double total = calculateTotal(cart.getProducts(), productResponses);
            cart.setTotal(total);

            redisTemplate.opsForValue().set("cart:" + cartId, cart);

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setTotal(total);

            return cartResponse;
      }

      @Transactional
      public void deleteCart(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);
            if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

            redisTemplate.delete("cart:" + cartId);
      }

      public CartResponse getCartById(String cartId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Cart cart = (Cart) redisTemplate.opsForValue().get("cart:" + cartId);
            if (cart == null) throw new AppException(ErrorCode.CART_NOT_FOUND);
            if (!cart.getUserId().equals(userId)) throw new AppException(ErrorCode.UNAUTHORIZED);

            CartResponse cartResponse = cartMapper.toCartResponse(cart);
            cartResponse.setTotal(cart.getTotal());

            return cartResponse;
      }

}