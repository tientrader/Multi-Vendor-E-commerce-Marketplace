package com.tien.promotion.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionCreationRequest {

      String name;
      String promoCode;
      String type;
      String description;

      Integer usageLimit;

      ConditionsCreationRequest conditions;
      DiscountCreationRequest discount;

      LocalDateTime startDate;
      LocalDateTime endDate;

}