package com.tien.order.service;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

      void createOrder(OrderCreationRequest request);
      List<OrderResponse> getAllOrders();
      List<OrderResponse> getMyOrders();
      List<OrderResponse> getOrdersByUsername(String username);
      OrderResponse getMyOrderByOrderId(Long orderId);
      void deleteOrder(Long orderId);

}