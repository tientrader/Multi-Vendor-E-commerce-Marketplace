package com.tien.promotion.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionsUpdateRequest {

      @Min(value = 0, message = "MIN_ORDER_VALUE_CANNOT_BE_NEGATIVE")
      Double minOrderValue;

      List<String> applicableProducts;
      List<String> applicableShops;

}