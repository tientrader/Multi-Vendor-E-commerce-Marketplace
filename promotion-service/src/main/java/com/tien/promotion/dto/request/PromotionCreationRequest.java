package com.tien.promotion.dto.request;

import com.tien.promotion.enums.PromotionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionCreationRequest {

      @NotNull(message = "NAME_IS_REQUIRED")
      @Size(min = 3, max = 255, message = "NAME_INVALID_SIZE")
      String name;

      @NotNull(message = "PROMO_CODE_IS_REQUIRED")
      @Size(min = 5, max = 20, message = "PROMO_CODE_INVALID_SIZE")
      String promoCode;

      @NotNull(message = "TYPE_IS_REQUIRED")
      PromotionType type;

      String description;

      @NotNull(message = "USAGE_LIMIT_IS_REQUIRED")
      Integer usageLimit;

      @NotNull(message = "CONDITIONS_ARE_REQUIRED")
      @Valid
      ConditionsCreationRequest conditions;

      @NotNull(message = "DISCOUNT_IS_REQUIRED")
      @Valid
      DiscountCreationRequest discount;

      @NotNull(message = "START_DATE_IS_REQUIRED")
      LocalDateTime startDate;

      @NotNull(message = "END_DATE_IS_REQUIRED")
      LocalDateTime endDate;

}