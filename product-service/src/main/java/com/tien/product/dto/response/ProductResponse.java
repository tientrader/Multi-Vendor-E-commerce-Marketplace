package com.tien.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

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
      int soldQuantity;
      List<ProductVariantResponse> variants;
      String categoryId;
      LocalDateTime createdAt;

}