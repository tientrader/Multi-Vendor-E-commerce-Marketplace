package com.tien.product.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {

      String variantId;
      double price;
      int stock;
      int soldQuantity;
      Map<String, Object> attributes;

}