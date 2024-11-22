package com.tien.promotion.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConditionsResponse {

      Double minOrderValue;
      List<String> applicableProducts;
      List<String> applicableShops;

}