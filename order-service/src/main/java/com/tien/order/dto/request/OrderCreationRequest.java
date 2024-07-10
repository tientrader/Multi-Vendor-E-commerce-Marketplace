package com.tien.order.dto.request;

import com.tien.order.entity.OrderItem;
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
      List<OrderItem> items;

}