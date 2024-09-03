package com.tien.order.service;

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
public class OrderService {

      ProductClient productClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;
      KafkaTemplate<String, Object> kafkaTemplate;

      @Transactional
      public void createOrder(OrderCreationRequest request) {
            String username = getCurrentUsername();

            double total = calculateOrderTotal(request.getItems());

            Order order = orderMapper.toOrder(request);
            order.setUsername(username);
            order.setTotal(total);
            order.setStatus("PENDING");

            for (OrderItemCreationRequest item : request.getItems()) {
                  int quantityToUpdate = -item.getQuantity();
                  productClient.updateStock(item.getProductId(), quantityToUpdate);
            }

            orderRepository.save(order);

            kafkaTemplate.send("order-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Order created successfully")
                    .body("Thank " + username + " for buying our products! \n" +
                            "The total amount is " + total)
                    .build());
      }

      private double calculateOrderTotal(List<OrderItemCreationRequest> items) {
            double total = 0.0;
            for (OrderItemCreationRequest item : items) {
                  ApiResponse<Double> priceResponse = productClient.getProductPriceById(item.getProductId());
                  Double price = priceResponse.getResult();
                  if (price != null) {
                        total += price * item.getQuantity();
                  } else {
                        log.warn("Price for product ID {} is not available", item.getProductId());
                  }
            }
            return total;
      }

      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getAllOrders() {
            List<Order> orders = orderRepository.findAll();

            if (orders.isEmpty()) {
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      public List<OrderResponse> getAllMyOrder() {
            String username = getCurrentUsername();

            List<Order> orders = orderRepository.findByUsername(username);
            if (orders.isEmpty()) {
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      public OrderResponse getMyOrderById(Long orderId) {
            String username = getCurrentUsername();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!order.getUsername().equals(username)) {
                  throw new AppException(ErrorCode.ORDER_IS_NOT_YOURS);
            }

            return orderMapper.toOrderResponse(order);
      }

      private String getCurrentUsername() {
            return ((Jwt) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getClaim("preferred_username");
      }

}