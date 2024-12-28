package com.tien.payment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionResponse {

      String id;
      String stripeCustomerId;
      String stripeSubscriptionId;
      Boolean success;

}