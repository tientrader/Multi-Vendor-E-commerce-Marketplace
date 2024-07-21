package com.tien.order.controller;

import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.dto.response.OrderResponse;
import com.tien.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

      OrderService orderService;

      @PostMapping("/create")
      public ApiResponse<Void> createOrder(@Valid @RequestBody OrderCreationRequest orderCreationRequest) {
            orderService.createOrder(orderCreationRequest);
            return ApiResponse.<Void>builder()
                    .message("Order created successfully")
                    .build();
      }

      @GetMapping("/{orderId}")
      public ApiResponse<OrderResponse> getMyOrder(@PathVariable Long orderId) {
            OrderResponse orderResponse = orderService.getMyOrder(orderId);
            return ApiResponse.<OrderResponse>builder()
                    .result(orderResponse)
                    .build();
      }

}