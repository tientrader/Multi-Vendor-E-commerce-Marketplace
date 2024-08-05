package com.tien.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {

      @NotBlank(message = "PRODUCT_NAME_IS_REQUIRED")
      String name;

      String description;

      @Positive(message = "PRICE_MUST_BE_POSITIVE")
      double price;

      @Positive(message = "STOCK_MUST_BE_POSITIVE")
      int stock;

      @NotBlank(message = "CATEGORY_ID_IS_REQUIRED")
      String categoryId;

}