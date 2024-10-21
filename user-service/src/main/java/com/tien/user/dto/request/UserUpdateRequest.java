package com.tien.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

      @Email(message = "INVALID_EMAIL")
      @NotBlank(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotNull(message = "PHONE_NUMBER_NULL")
      @Pattern(regexp = "^\\+?[0-9]{1,3}[0-9]{9,14}$", message = "INVALID_PHONE_NUMBER")
      String phoneNumber;

      @NotNull(message = "FIRSTNAME_NULL")
      String firstName;

      @NotNull(message = "LASTNAME_NULL")
      String lastName;

}