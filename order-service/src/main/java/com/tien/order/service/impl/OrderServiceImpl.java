package com.tien.order.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.request.OrderItemCreationRequest;
import com.tien.event.dto.StripeChargeRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;
import com.tien.order.httpclient.ProductClient;
import com.tien.order.kafka.KafkaProducer;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;
import com.tien.order.service.OrderService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

      ProductClient productClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;
      KafkaProducer kafkaProducer;

      @Override
      @Transactional
      public OrderResponse createOrder(OrderCreationRequest request) {
            String username = getCurrentUsername();

            validateStockAvailability(request.getItems());

            Order order = orderMapper.toOrder(request);
            order.setUsername(username);
            order.setTotal(request.getTotal());
            order.setStatus("PENDING");
            orderRepository.save(order);

            switch (request.getPaymentMethod().toUpperCase()) {
                  case "CARD":
                        kafkaProducer.send("payment-request", StripeChargeRequest.builder()
                                .orderId(order.getOrderId())
                                .username(username)
                                .amount(order.getTotal())
                                .stripeToken(request.getPaymentToken())
                                .email(request.getEmail())
                                .build());
                        break;

                  case "COD":
                        break;
            }

            updateStockAndSoldQuantity(request.getItems());
            orderRepository.save(order);

            kafkaProducer.send("order-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Order created successfully")
                    .body("Thank you for your purchase, " + username)
                    .build());

            return orderMapper.toOrderResponse(order);
      }

      @Override
      @Transactional
      public OrderResponse buyNow(OrderCreationRequest request) {
            String username = getCurrentUsername();

            if (request.getItems().size() > 1) {
                  log.error("Buy Now can only handle a single product: request={}", request);
                  throw new AppException(ErrorCode.MORE_THAN_ONE_PRODUCT);
            }

            validateStockAvailability(request.getItems());

            Order order = orderMapper.toOrder(request);
            order.setUsername(username);
            order.setTotal(calculateOrderTotal(request.getItems()));
            order.setStatus("PENDING");

            switch (request.getPaymentMethod().toUpperCase()) {
                  case "CARD":
                        kafkaProducer.send("payment-request", StripeChargeRequest.builder()
                                .username(username)
                                .amount(order.getTotal())
                                .stripeToken(request.getPaymentToken())
                                .email(request.getEmail())
                                .build());
                        break;

                  case "COD":
                        break;
            }

            updateStockAndSoldQuantity(request.getItems());
            orderRepository.save(order);

            kafkaProducer.send("order-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Order created successfully")
                    .body("Thank you for your purchase, " + username)
                    .build());

            return orderMapper.toOrderResponse(order);
      }

      @Override
      @Transactional
      public void updateOrderStatus(Long orderId, String newStatus) {
            Order order = findOrderById(orderId);
            order.setStatus(newStatus);
            orderRepository.save(order);
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
            return orderRepository.findAll()
                    .stream()
                    .map(orderMapper::toOrderResponse)
                    .toList();
      }

      @Override
      public List<OrderResponse> getMyOrders() {
            return findOrdersByUsername(getCurrentUsername())
                    .stream()
                    .map(orderMapper::toOrderResponse)
                    .toList();
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<OrderResponse> getOrdersByUsername(String username) {
            return findOrdersByUsername(username)
                    .stream()
                    .map(orderMapper::toOrderResponse)
                    .toList();
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
            return items.stream().mapToDouble(item -> {
                  try {
                        return productClient.getProductPriceById(item.getProductId(), item.getVariantId()).getResult() * item.getQuantity();
                  } catch (FeignException e) {
                        if (e.status() == 404) {
                              throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                        }
                        log.error("Error fetching price for productId={}, variantId={}: {}",
                                item.getProductId(), item.getVariantId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            }).sum();
      }

      private void validateStockAvailability(List<OrderItemCreationRequest> items) {
            items.forEach(item -> {
                  try {
                        int stockQuantity = productClient.getProductStockById(item.getProductId(), item.getVariantId()).getResult();
                        if (stockQuantity < item.getQuantity()) {
                              log.error("Out of stock for productId={}, variantId={}, requestedQuantity={}, availableQuantity={}",
                                      item.getProductId(), item.getVariantId(), item.getQuantity(), stockQuantity);
                              throw new AppException(ErrorCode.OUT_OF_STOCK);
                        }
                  } catch (FeignException e) {
                        if (e.status() == 404) {
                              throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                        }
                        log.error("Error fetching stock for productId={}, variantId={}: {}",
                                item.getProductId(), item.getVariantId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            });
      }

      private void updateStockAndSoldQuantity(List<OrderItemCreationRequest> items) {
            items.forEach(item -> {
                  try {
                        productClient.updateStockAndSoldQuantity(item.getProductId(), item.getVariantId(), item.getQuantity());
                  } catch (FeignException e) {
                        log.error("Error updating stock for productId={}, variantId={}: {}",
                                item.getProductId(), item.getVariantId(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }
            });
      }

      private Order findOrderById(Long orderId) {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
      }

      private List<Order> findOrdersByUsername(String username) {
            List<Order> orders = orderRepository.findByUsername(username);
            if (orders.isEmpty()) {
                  throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }
            return orders;
      }

}