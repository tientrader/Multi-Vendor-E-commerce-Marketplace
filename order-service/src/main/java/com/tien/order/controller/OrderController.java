package com.tien.order.controller;

import com.tien.order.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

      OrderService orderService;

//      @PostMapping("/create")
//      ApiResponse<OrderResponse> createOder(@RequestBody @Valid OrderCreationRequest request) {
//            return ApiResponse.<OrderResponse>builder()
//                    .result(orderService.createOrder(request))
//                    .build();
//      }

}