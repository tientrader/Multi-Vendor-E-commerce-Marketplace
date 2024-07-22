package com.tien.cart.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {

      String userId;
      String email;
      List<OrderItemCreationRequest> items;
      double total;
      String status;

}