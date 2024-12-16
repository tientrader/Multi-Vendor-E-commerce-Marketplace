package com.tien.user.httpclient.request;

import com.tien.user.enums.PackageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionSessionRequest {

      String email;
      String username;
      PackageType packageType;

}