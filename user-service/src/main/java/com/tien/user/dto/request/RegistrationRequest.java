package com.tien.user.dto.request;

import com.tien.user.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationRequest {

    @Size(min = 4, message = "INVALID_USERNAME")
    String username;

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    String email;

    @NotNull(message = "FIRSTNAME_NULL")
    String firstName;

    @NotNull(message = "LASTNAME_NULL")
    String lastName;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    @NotNull(message = "DOB_NULL")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dob;

}