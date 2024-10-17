package com.tien.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

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
      Map<String, Object> additionalInfo = new HashMap<>();

}