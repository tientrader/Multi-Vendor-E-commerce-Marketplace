package com.tien.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

      @NotNull(message = "FIRSTNAME_IS_REQUIRED")
      String firstName;

      @NotNull(message = "LASTNAME_IS_REQUIRED")
      String lastName;

}