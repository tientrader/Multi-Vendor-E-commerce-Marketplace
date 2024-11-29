package com.tien.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationRequest {

      @NotNull(message = "PRODUCT_ID_IS_REQUIRED")
      String productId;

      @NotNull(message = "VARIANT_ID_IS_REQUIRED")
      String variantId;

      @NotNull(message = "QUANTITY_IS_REQUIRED")
      @Min(value = 1, message = "QUANTITY_MUST_BE_AT_LEAST_1")
      Integer quantity;

}