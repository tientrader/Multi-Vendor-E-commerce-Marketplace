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
      public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderCreationRequest orderCreationRequest) {
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.createOrder(orderCreationRequest))
                    .build();
      }

      @PostMapping("/create-from-cart")
      public ApiResponse<Void> createOrderFromCart(@Valid @RequestBody OrderCreationRequest orderCreationRequest) {
            orderService.createOrderFromCart(orderCreationRequest);
            return ApiResponse.<Void>builder()
                    .message("Order created from cart successfully")
                    .build();
      }

      @DeleteMapping("/{orderId}")
      public ApiResponse<Void> deleteOrder(@PathVariable Long orderId) {
            orderService.deleteOrder(orderId);
            return ApiResponse.<Void>builder()
                    .message("Order deleted successfully")
                    .build();
      }

      @GetMapping("/orders")
      public ApiResponse<List<OrderResponse>> getAllOrders() {
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderService.getAllOrders())
                    .build();
      }

      @GetMapping("/my-orders")
      public ApiResponse<List<OrderResponse>> getMyOrders() {
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderService.getMyOrders())
                    .build();
      }

      @GetMapping("/{username}")
      public ApiResponse<List<OrderResponse>> getOrdersByUsername(@PathVariable String username) {
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderService.getOrdersByUsername(username))
                    .build();
      }

      @GetMapping("/my-order/{orderId}")
      public ApiResponse<OrderResponse> getMyOrderByOrderId(@PathVariable Long orderId) {
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.getMyOrderByOrderId(orderId))
                    .build();
      }

}