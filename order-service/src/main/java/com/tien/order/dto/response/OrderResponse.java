package com.tien.order.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

      Long orderId;
      String username;
      String email;
      List<OrderItemResponse> items;
      double total;
      String status;

}