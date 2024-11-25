package com.tien.promotion.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountUpdateRequest {

      @NotNull(message = "AMOUNT_IS_REQUIRED")
      @Min(value = 0, message = "AMOUNT_CANNOT_BE_NEGATIVE")
      Double amount;

      @NotNull(message = "PERCENTAGE_IS_REQUIRED")
      @Min(value = 0, message = "PERCENTAGE_CANNOT_BE_NEGATIVE")
      Double percentage;

      @NotNull(message = "MAX_DISCOUNT_VALUE_IS_REQUIRED")
      @Min(value = 0, message = "MAX_DISCOUNT_VALUE_CANNOT_BE_NEGATIVE")
      Double maxDiscountValue;

}