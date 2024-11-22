package com.tien.promotion.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conditions {

      Double minOrderValue;
      List<String> applicableProducts;
      List<String> applicableShops;

}