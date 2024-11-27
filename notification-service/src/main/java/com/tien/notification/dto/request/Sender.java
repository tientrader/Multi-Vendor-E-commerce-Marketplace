package com.tien.notification.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sender {

      @NotNull(message = "SENDER_NAME_IS_REQUIRED")
      String name;

      @NotNull(message = "SENDER_EMAIL_IS_REQUIRED")
      @Email(message = "INVALID_EMAIL")
      String email;

}