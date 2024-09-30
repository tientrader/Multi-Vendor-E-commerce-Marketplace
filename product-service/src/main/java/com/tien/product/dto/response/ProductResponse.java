package com.tien.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

      String id;
      String name;
      String description;
      double price;
      int stock;
      int soldQuantity;
      String categoryId;

}