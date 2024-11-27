package com.tien.payment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionRequest {

      String stripeToken;

      @NotNull(message = "EMAIL_IS_REQUIRED")
      @Email(message = "INVALID_EMAIL")
      String email;

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      String packageType;

      long numberOfLicense;

}