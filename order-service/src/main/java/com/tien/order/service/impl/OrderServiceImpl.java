package com.tien.order.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.request.OrderItemCreationRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;
import com.tien.order.httpclient.ProductClient;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;

import com.tien.order.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

      ProductClient productClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;
      KafkaTemplate<String, Object> kafkaTemplate;

      @Override
      @Transactional
      public void createOrder(OrderCreationRequest request) {
            log.info("Starting order creation for user: {}", request.getEmail());
            String username = getCurrentUsername();

            Order order = orderMapper.toOrder(request);
            order.setUsername(username);
            order.setTotal(calculateOrderTotal(request.getItems()));
            order.setStatus("PENDING");

            log.info("Order created for user: {}, total amount: {}", username, order.getTotal());

            updateStockAndSoldQuantity(request.getItems());

            orderRepository.save(order);
            log.info("Order saved successfully for user: {}", username);

            kafkaTemplate.send("order-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Order created successfully")
                    .body("Thank " + username + " for buying our products!")
                    .build());
            log.info("Notification email sent to: {}", request.getEmail());
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getAllOrders() {
            log.info("Fetching all orders.");
            List<Order> orders = orderRepository.findAll();

            if (orders.isEmpty()) {
                  log.warn("No orders found.");
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public List<OrderResponse> getMyOrders() {
            String username = getCurrentUsername();
            log.info("(getMyOrders) Fetching orders for user: {}", username);

            List<Order> orders = orderRepository.findByUsername(username);
            if (orders.isEmpty()) {
                  log.warn("(getMyOrders) No orders found for user {}", username);
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getOrdersByUsername(String username) {
            log.info("(getOrdersByUsername) Fetching orders for user: {}", username);
            List<Order> orders = orderRepository.findByUsername(username);

            if (orders.isEmpty()) {
                  log.warn("(getOrdersByUsername) No orders found for user {}", username);
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public OrderResponse getMyOrderByOrderId(Long orderId) {
            String username = getCurrentUsername();
            log.info("Fetching order ID: {} for user: {}", orderId, username);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!order.getUsername().equals(username)) {
                  log.warn("User {} attempted to access an order that is not theirs.", username);
                  throw new AppException(ErrorCode.ORDER_IS_NOT_YOURS);
            }

            log.info("Order ID: {} fetched successfully for user: {}", orderId, username);
            return orderMapper.toOrderResponse(order);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public void deleteOrder(Long orderId) {
            log.info("Deleting order ID: {}", orderId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            orderRepository.delete(order);
            log.info("Order with ID {} has been deleted.", orderId);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private double calculateOrderTotal(List<OrderItemCreationRequest> items) {
            log.info("Calculating total for order items.");
            double total = 0.0;
            for (OrderItemCreationRequest item : items) {
                  ApiResponse<Double> priceResponse = productClient.getProductPriceById(item.getProductId(), item.getVariantId());
                  Double price = priceResponse.getResult();
                  if (price != null) {
                        total += price * item.getQuantity();
                  } else {
                        log.warn("Price for product ID {} is not available", item.getProductId());
                  }
            }
            log.info("Total calculated: {}", total);
            return total;
      }

      private void updateStockAndSoldQuantity(List<OrderItemCreationRequest> items) {
            log.info("Updating product stock and sold quantity for {} items.", items.size());
            for (OrderItemCreationRequest item : items) {
                  int quantityToUpdate = item.getQuantity();
                  try {
                        log.info("Updating stock and sold quantity for product ID: {} with quantity: {}", item.getProductId(), quantityToUpdate);
                        productClient.updateStockAndSoldQuantity(item.getProductId(), item.getVariantId(), quantityToUpdate);
                        log.info("Successfully updated stock and sold quantity for product ID: {}", item.getProductId());
                  } catch (Exception e) {
                        log.error("Error updating stock and sold quantity for product ID {}: {}", item.getProductId(), e.getMessage());
                        throw new AppException(ErrorCode.OUT_OF_STOCK);
                  }
            }
      }

}