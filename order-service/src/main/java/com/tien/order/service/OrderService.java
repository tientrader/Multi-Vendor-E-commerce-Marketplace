package com.tien.order.service;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.entity.Order;
import com.tien.order.entity.OrderItem;
import com.tien.order.httpclient.ProductClient;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

      OrderRepository orderRepository;
      ProductClient productClient;
      OrderMapper orderMapper;

      @Transactional
      public OrderResponse createOrder(OrderCreationRequest request) {
            List<OrderItem> orderItems = request.getItems();

            Order order = orderMapper.toOrder(request);
            order.setItems(orderItems);
            order.setTotal(calculateTotal(orderItems));
            order.setStatus("PENDING");

            Order savedOrder = orderRepository.save(order);

            for (OrderItem item : savedOrder.getItems()) {
                  productClient.updateStock(item.getProductId(), item.getQuantity());
            }

            savedOrder.setStatus("COMPLETED");
            savedOrder = orderRepository.save(savedOrder);

            return orderMapper.toOrderResponse(savedOrder);
      }

      private double calculateTotal(List<OrderItem> orderItems) {
            return orderItems.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getPrice())
                    .sum();
      }

}