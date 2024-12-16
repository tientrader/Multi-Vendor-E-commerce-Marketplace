package com.tien.payment.dto.request;

import com.tien.payment.enums.PackageType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionSessionRequest {

      @NotNull(message = "EMAIL_IS_REQUIRED")
      @Email(message = "INVALID_EMAIL")
      String email;

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      PackageType packageType;

}