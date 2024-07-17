package com.tien.order.service;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderItemResponse;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.dto.response.ProductResponse;
import com.tien.order.dto.response.UserProfileResponse;
import com.tien.order.entity.Order;
import com.tien.order.entity.OrderItem;
import com.tien.order.httpclient.ProductClient;
import com.tien.order.httpclient.ProfileClient;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

      ProductClient productClient;
      ProfileClient profileClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;

      @Transactional
      public OrderResponse createOrder(OrderCreationRequest orderRequest) {
            log.info("Creating order for user: {}", orderRequest.getUserId());

            List<OrderItem> orderItems = orderMapper.toOrderItems(orderRequest.getItems());
            log.info("Mapped {} order items", orderItems.size());

            double total = calculateTotal(orderItems);
            log.info("Total calculated: {}", total);

            Order order = orderMapper.toOrder(orderRequest, orderItems);
            order.setTotal(total);
            log.info("Order object created: {}", order);

            orderRepository.save(order);
            log.info("Order saved in database");

            UserProfileResponse userProfile = fetchUserProfile(order.getUserId());
            log.info("Fetched user profile: {}", userProfile);

            List<OrderItemResponse> orderItemResponses = mapOrderItemsToOrderItemResponses(orderItems);
            log.info("Mapped order item responses: {}", orderItemResponses);

            OrderResponse orderResponse = orderMapper.toOrderResponse(order, orderItemResponses, userProfile);
            log.info("Created order response: {}", orderResponse);

            return orderResponse;
      }

      private double calculateTotal(List<OrderItem> orderItems) {
            double total = orderItems.stream()
                    .mapToDouble(item -> {
                          ProductResponse product = productClient.getProductById(item.getProductId());
                          Double price = Optional.ofNullable(product).map(ProductResponse::getPrice)
                                  .orElseThrow(() -> new RuntimeException("Price is null for item with productId: " + item.getProductId()));

                          return price * item.getQuantity();
                    }).sum();

            log.info("Total calculated:(calculateTotal) {}", total);
            return total;
      }

      public List<OrderItemResponse> mapOrderItemsToOrderItemResponses(List<OrderItem> orderItems) {
            return orderItems.stream()
                    .map(orderItem -> {
                          String productId = orderItem.getProductId();
                          ProductResponse productResponse = productClient.getProductById(productId);
                          return orderMapper.toOrderItemResponse(orderItem, productResponse);
                    })
                    .collect(Collectors.toList());
      }

      private UserProfileResponse fetchUserProfile(String userId) {
            return profileClient.getProfileByUserId(userId);
      }

}