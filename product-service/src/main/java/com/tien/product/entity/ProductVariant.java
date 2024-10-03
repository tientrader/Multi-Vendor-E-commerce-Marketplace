package com.tien.product.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {

      @Id
      @Builder.Default
      String variantId = UUID.randomUUID().toString();

      double price;
      int stock;
      Map<String, Object> attributes;

}