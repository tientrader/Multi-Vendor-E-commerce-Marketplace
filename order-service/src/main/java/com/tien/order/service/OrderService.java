package com.tien.order.service;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

      OrderResponse createOrder(OrderCreationRequest request);

      OrderResponse buyNow(OrderCreationRequest request);

      void updateOrderStatus(Long orderId, String newStatus);

      void deleteOrder(Long orderId);

      List<OrderResponse> getAllOrders();

      List<OrderResponse> getMyOrders();

      List<OrderResponse> getOrdersByUsername(String username);

      OrderResponse getMyOrderByOrderId(Long orderId);

}