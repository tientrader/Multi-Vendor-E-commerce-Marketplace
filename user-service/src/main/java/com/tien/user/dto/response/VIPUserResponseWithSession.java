package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserResponseWithSession {

      String id;
      String sessionUrl;
      String username;
      String email;

}