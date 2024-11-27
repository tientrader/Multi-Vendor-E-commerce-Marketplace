package com.tien.user.dto.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationParam {

      String username;
      boolean enabled;
      String email;
      boolean emailVerified;
      String firstName;
      String lastName;
      List<Credential> credentials;
      Map<String, List<String>> attributes;

}