package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionResponse {

      String id;
      String username;
      String stripeCustomerId;
      String stripeSubscriptionId;
      String stripePaymentMethodId;
      String status;
      String message;

}