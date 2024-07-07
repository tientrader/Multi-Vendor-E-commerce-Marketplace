package com.tien.identity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NotNull(message = "USERNAME_NULL")
    String username;

    @NotNull(message = "PASSWORD_NULL")
    String password;

}