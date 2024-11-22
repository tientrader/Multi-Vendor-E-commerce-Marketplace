package com.tien.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantUpdateRequest {

      @NotNull(message = "PRICE_IS_REQUIRED")
      @Positive(message = "PRICE_MUST_BE_POSITIVE")
      Double price;

      @NotNull(message = "STOCK_IS_REQUIRED")
      @Positive(message = "STOCK_MUST_BE_POSITIVE")
      Integer stock;

      Map<String, Object> attributes;

}