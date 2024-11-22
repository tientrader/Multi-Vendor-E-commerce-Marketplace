package com.tien.promotion.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionsUpdateRequest {

      Double minOrderValue;
      List<String> applicableProducts;
      List<String> applicableShops;

}