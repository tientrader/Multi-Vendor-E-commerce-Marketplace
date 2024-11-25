package com.tien.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserRequestWithSession {

      @Email(message = "INVALID_EMAIL")
      @NotNull(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotNull(message = "PACKAGE_TYPE_IS_REQUIRED")
      String packageType;

}