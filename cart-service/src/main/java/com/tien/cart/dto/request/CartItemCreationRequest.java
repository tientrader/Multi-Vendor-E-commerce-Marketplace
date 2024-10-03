package com.tien.cart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationRequest {

      @NotBlank(message = "PRODUCT_ID_IS_REQUIRED")
      String productId;

      @NotNull(message = "VARIANT_ID_IS_REQUIRED")
      String variantId;

      @NotNull(message = "QUANTITY_IS_REQUIRED")
      @Positive(message = "QUANTITY_MUST_BE_POSITIVE")
      Integer quantity;

}