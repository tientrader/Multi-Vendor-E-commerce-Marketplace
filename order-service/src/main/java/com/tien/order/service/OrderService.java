package com.tien.order.service;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

      ProductClient productClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;
      KafkaTemplate<String, String> kafkaTemplate;

      public void createOrder(OrderCreationRequest orderCreationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            double total = calculateOrderTotal(orderCreationRequest.getItems());

            Order order = orderMapper.toOrder(orderCreationRequest);
            order.setUserId(userId);
            order.setTotal(total);

            for (OrderItemCreationRequest item : orderCreationRequest.getItems()) {
                  int quantityToUpdate = -item.getQuantity();
                  productClient.updateStock(item.getProductId(), quantityToUpdate);
            }

            orderRepository.save(order);

            kafkaTemplate.send("order-successful", "Order ID: " + order.getOrderId() + " created successfully!");
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

      public OrderResponse getMyOrder(Long orderId) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            if (!order.getUserId().equals(userId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return orderMapper.toOrderResponse(order);
      }

}