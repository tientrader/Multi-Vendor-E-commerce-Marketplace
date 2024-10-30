package com.tien.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeChargeRequest {

      String stripeToken;
      String username;
      String email;
      Double amount;

}