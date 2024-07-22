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

import java.util.List;

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

      @GetMapping("/my-orders")
      public ApiResponse<List<OrderResponse>> getAllMyOrder() {
            List<OrderResponse> orderResponses = orderService.getAllMyOrder();
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderResponses)
                    .build();
      }

      @GetMapping("/{orderId}")
      public ApiResponse<OrderResponse> getMyOrderById(@PathVariable Long orderId) {
            OrderResponse orderResponse = orderService.getMyOrderById(orderId);
            return ApiResponse.<OrderResponse>builder()
                    .result(orderResponse)
                    .build();
      }

}