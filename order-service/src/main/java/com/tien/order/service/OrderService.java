package com.tien.order.service;

import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.request.OrderItemCreationRequest;
import com.tien.order.entity.Order;
import com.tien.order.httpclient.ProductClient;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {

      ProductClient productClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;

      public void createOrder(OrderCreationRequest orderCreationRequest) {
            Order order = orderMapper.toOrder(orderCreationRequest);

            for (OrderItemCreationRequest item : orderCreationRequest.getItems()) {
                  int quantityToUpdate = -item.getQuantity();
                  productClient.updateStock(item.getProductId(), quantityToUpdate);
            }

            orderRepository.save(order);
      }

}