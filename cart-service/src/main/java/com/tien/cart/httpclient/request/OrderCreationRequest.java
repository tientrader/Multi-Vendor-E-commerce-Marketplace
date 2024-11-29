package com.tien.cart.httpclient.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {

      String email;
      List<OrderItemCreationRequest> items;
      double total;
      String paymentMethod;
      String paymentToken;

}