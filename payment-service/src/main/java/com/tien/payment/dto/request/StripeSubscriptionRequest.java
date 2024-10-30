package com.tien.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionRequest {

      String stripeToken;
      String email;
      String packageType;
      String username;
      long numberOfLicense;

}