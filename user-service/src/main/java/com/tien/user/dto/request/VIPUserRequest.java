package com.tien.user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserRequest {

      String username;
      String email;
      String stripeToken;
      String priceId;
      long numberOfLicense;

}