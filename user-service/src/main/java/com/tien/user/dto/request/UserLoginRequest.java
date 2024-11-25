package com.tien.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginRequest {

      @Size(min = 4, message = "INVALID_USERNAME")
      String username;

      @Size(min = 8, message = "INVALID_PASSWORD")
      String password;

}