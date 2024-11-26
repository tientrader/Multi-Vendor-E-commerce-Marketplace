package com.tien.user.dto.request;

import com.tien.user.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

      @Size(min = 4, message = "INVALID_USERNAME")
      String username;

      @Size(min = 8, message = "INVALID_PASSWORD")
      String password;

      @Email(message = "INVALID_EMAIL")
      @NotNull(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotNull(message = "PHONE_NUMBER_IS_REQUIRED")
      @Pattern(regexp = "^\\+?[0-9]{1,3}[0-9]{9,14}$", message = "INVALID_PHONE_NUMBER")
      String phoneNumber;

      @NotNull(message = "FIRSTNAME_IS_REQUIRED")
      String firstName;

      @NotNull(message = "LASTNAME_IS_REQUIRED")
      String lastName;

      @DobConstraint(min = 0, message = "INVALID_DOB")
      @NotNull(message = "DOB_IS_REQUIRED")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate dob;

}