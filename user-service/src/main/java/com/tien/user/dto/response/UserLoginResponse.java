package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLoginResponse {

      String accessToken;
      String expiresIn;
      String refreshToken;
      String refreshExpiresIn;

}