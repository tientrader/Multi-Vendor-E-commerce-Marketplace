package com.tien.order.controller;

import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

      OrderService orderService;

      @PostMapping
      ApiResponse<OrderResponse> createOder(@RequestBody @Valid OrderCreationRequest request) {
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.createOrder(request))
                    .build();
      }

}