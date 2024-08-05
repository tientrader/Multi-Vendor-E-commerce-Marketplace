package com.tien.identity.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.tien.identity.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 4, message = "USERNAME_INVALID")
    @NotBlank(message = "USERNAME_NULL")
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD")
    @NotNull(message = "PASSWORD_NULL")
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
    LocalDate dob;

    @NotNull(message = "CITY_NULL")
    String city;

}