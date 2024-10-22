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

      @NotNull(message = "FIRSTNAME_NULL")
      String firstName;

      @NotNull(message = "LASTNAME_NULL")
      String lastName;

}