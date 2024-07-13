package com.tien.order.service;

import com.tien.order.httpclient.ProductClient;
import com.tien.order.httpclient.ProfileClient;
import com.tien.order.mapper.OrderMapper;
import com.tien.order.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

      ProductClient productClient;
      ProfileClient profileClient;
      OrderRepository orderRepository;
      OrderMapper orderMapper;

}