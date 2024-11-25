package com.tien.shop.httpclient.response;

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
      List<ProductVariantResponse> variants;
      String categoryId;
      List<String> imageUrls;
      LocalDateTime createdAt;
      LocalDateTime updatedAt;

}