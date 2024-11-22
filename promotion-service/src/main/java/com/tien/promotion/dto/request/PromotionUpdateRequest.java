package com.tien.promotion.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionUpdateRequest {

      String name;
      String type;
      String description;

      Integer usageLimit;

      ConditionsUpdateRequest conditions;
      DiscountUpdateRequest discount;

      LocalDateTime startDate;
      LocalDateTime endDate;

}