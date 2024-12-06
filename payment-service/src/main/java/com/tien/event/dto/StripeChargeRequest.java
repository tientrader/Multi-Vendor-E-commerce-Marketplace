package com.tien.event.dto;

import jakarta.validation.constraints.Email;
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

      String stripeToken;

      String username;

      @NotNull(message = "EMAIL_IS_REQUIRED")
      @Email(message = "INVALID_EMAIL")
      String email;

      @NotNull(message = "AMOUNT_IS_REQUIRED")
      @Positive(message = "AMOUNT_MUST_BE_POSITIVE")
      Double amount;

}