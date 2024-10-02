package com.tien.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

      String id;
      String shopId;
      String name;
      String description;
      double price;
      int stock;
      int soldQuantity;
      LocalDateTime createdAt;
      String categoryId;

}