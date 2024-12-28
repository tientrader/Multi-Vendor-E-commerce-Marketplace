package com.tien.payment.dto.request;

import com.tien.payment.enums.PackageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionRequest {

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      PackageType packageType;

      String stripeToken;
      long numberOfLicense;

}