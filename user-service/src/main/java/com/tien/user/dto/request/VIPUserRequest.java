package com.tien.user.dto.request;

import com.tien.user.enums.PackageType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserRequest {

      @Email(message = "INVALID_EMAIL")
      @NotNull(message = "EMAIL_IS_REQUIRED")
      String email;

      String stripeToken;

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      PackageType packageType;

      long numberOfLicense;

}