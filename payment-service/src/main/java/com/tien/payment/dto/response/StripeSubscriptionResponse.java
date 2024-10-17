package com.tien.payment.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionResponse {

      String username;
      String stripeCustomerId;
      String stripeSubscriptionId;
      String stripePaymentMethodId;
      String subscriptionId;
      String status;
      String message;

}