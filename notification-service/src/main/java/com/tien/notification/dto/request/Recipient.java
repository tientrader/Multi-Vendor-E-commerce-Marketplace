package com.tien.notification.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Recipient {

      String name;

      @NotNull(message = "EMAIL_IS_REQUIRED")
      @Email(message = "INVALID_EMAIL")
      String email;

}