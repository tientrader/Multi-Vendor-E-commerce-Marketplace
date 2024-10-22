package com.tien.order.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.request.OrderItemCreationRequest;
import com.tien.order.dto.request.StripeChargeRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.dto.response.StripeChargeResponse;
import com.tien.order.entity.Order;
import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;
import com.tien.order.httpclient.PaymentClient;
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
      PaymentClient paymentClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;
      KafkaTemplate<String, Object> kafkaTemplate;

      @Override
      @Transactional
      public OrderResponse createOrder(OrderCreationRequest request) {
            String username = getCurrentUsername();

            validateStockAvailability(request.getItems());

            Order order = orderMapper.toOrder(request);
            order.setUsername(username);
            order.setTotal(calculateOrderTotal(request.getItems()));
            order.setStatus("PENDING");

            if ("CARD".equalsIgnoreCase(request.getPaymentMethod())) {
                  StripeChargeRequest stripeChargeRequest = new StripeChargeRequest();
                  stripeChargeRequest.setUsername(username);
                  stripeChargeRequest.setAmount(order.getTotal());
                  stripeChargeRequest.setStripeToken(request.getPaymentToken());
                  stripeChargeRequest.setEmail(request.getEmail());

                  ApiResponse<StripeChargeResponse> paymentResponse = paymentClient.charge(stripeChargeRequest);

                  if (paymentResponse.getResult() != null && paymentResponse.getResult().getSuccess()) {
                        order.setStatus("PAID");
                  } else {
                        log.error("Payment failed for user {}: {}", username, paymentResponse.getMessage());
                        throw new AppException(ErrorCode.PAYMENT_FAIL);
                  }
            } else if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
                  order.setStatus("PENDING");
            } else {
                  log.error("Invalid payment method: {}", request.getPaymentMethod());
                  throw new IllegalArgumentException("Invalid payment method: " + request.getPaymentMethod());
            }

            updateStockAndSoldQuantity(request.getItems());
            orderRepository.save(order);

            kafkaTemplate.send("order-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Order created successfully")
                    .body("Thank you for your purchase, " + username)
                    .build());

            return orderMapper.toOrderResponse(order);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public void deleteOrder(Long orderId) {
            orderRepository.delete(findOrderById(orderId));
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getAllOrders() {
            return orderRepository.findAll().stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public List<OrderResponse> getMyOrders() {
            String username = getCurrentUsername();
            return findOrdersByUsername(username).stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getOrdersByUsername(String username) {
            return findOrdersByUsername(username).stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
      }

      @Override
      public OrderResponse getMyOrderByOrderId(Long orderId) {
            String username = getCurrentUsername();
            Order order = findOrderById(orderId);

            if (!order.getUsername().equals(username)) {
                  log.error("User {} attempted to access another user's order: orderId={}", username, orderId);
                  throw new AppException(ErrorCode.ORDER_IS_NOT_YOURS);
            }

            return orderMapper.toOrderResponse(order);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private double calculateOrderTotal(List<OrderItemCreationRequest> items) {
            return items.stream()
                    .mapToDouble(item ->
                            productClient.getProductPriceById(item.getProductId(), item.getVariantId())
                                    .getResult() * item.getQuantity()
                    )
                    .sum();
      }

      private void validateStockAvailability(List<OrderItemCreationRequest> items) {
            items.forEach(item -> {
                  int stockQuantity = productClient.getProductStockById(
                          item.getProductId(),
                          item.getVariantId()
                  ).getResult();

                  if (stockQuantity < item.getQuantity()) {
                        log.error("Out of stock for productId={}, variantId={}, requestedQuantity={}, availableQuantity={}",
                                item.getProductId(), item.getVariantId(), item.getQuantity(), stockQuantity);
                        throw new AppException(ErrorCode.OUT_OF_STOCK);
                  }
            });
      }

      private void updateStockAndSoldQuantity(List<OrderItemCreationRequest> items) {
            items.forEach(item -> productClient.updateStockAndSoldQuantity(
                    item.getProductId(),
                    item.getVariantId(),
                    item.getQuantity()
            ));
      }

      private Order findOrderById(Long orderId) {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                          log.error("Order not found: orderId={}", orderId);
                          return new AppException(ErrorCode.ORDER_NOT_FOUND);
                    });
      }

      private List<Order> findOrdersByUsername(String username) {
            List<Order> orders = orderRepository.findByUsername(username);
            if (orders.isEmpty()) {
                  log.error("No orders found for username={}", username);
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }
            return orders;
      }

}