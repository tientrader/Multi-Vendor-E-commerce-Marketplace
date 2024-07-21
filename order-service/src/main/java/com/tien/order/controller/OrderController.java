package com.tien.order.controller;

import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.OrderCreationRequest;
import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;
import com.tien.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

      OrderService orderService;

      @PostMapping("/create")
      public ApiResponse<Void> createOrder(@Valid @RequestBody OrderCreationRequest orderCreationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            if (userId == null) throw new AppException(ErrorCode.UNAUTHORIZED);

            orderService.createOrder(orderCreationRequest);
            return ApiResponse.<Void>builder()
                    .message("Order created successfully")
                    .build();
      }

}