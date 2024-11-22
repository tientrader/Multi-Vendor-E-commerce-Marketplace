package com.tien.promotion.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCreationRequest {

      Double amount;
      Double percentage;
      Double maxDiscountValue;

}