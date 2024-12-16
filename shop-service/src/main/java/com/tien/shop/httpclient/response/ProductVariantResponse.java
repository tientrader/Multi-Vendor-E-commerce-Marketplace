package com.tien.shop.httpclient.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {

      String variantId;
      double price;
      int stock;
      int soldQuantity;
      Map<String, Object> attributes;

}