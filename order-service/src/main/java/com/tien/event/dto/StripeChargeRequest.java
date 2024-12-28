package com.tien.event.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeChargeRequest {

      Long orderId;
      String stripeToken;
      String username;
      String email;

      @NotNull(message = "AMOUNT_IS_REQUIRED")
      @Positive(message = "AMOUNT_MUST_BE_POSITIVE")
      Double amount;

}