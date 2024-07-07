package com.tien.identity.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.tien.identity.validator.DobConstraint;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @Size(min = 8, message = "INVALID_PASSWORD")
    @NotNull(message = "PASSWORD_NULL")
    String password;

    @NotNull(message = "FIRSTNAME_NULL")
    String firstName;
    @NotNull(message = "LASTNAME_NULL")
    String lastName;

    @DobConstraint(min = 10, message = "INVALID_DOB")
    @NotNull(message = "DOB_NULL")
    LocalDate dob;

    @NotNull(message = "CITY_NULL")
    String city;

    List<String> roles;

}