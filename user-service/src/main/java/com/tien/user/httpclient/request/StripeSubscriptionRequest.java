package com.tien.user.httpclient.request;

import com.tien.user.enums.PackageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscriptionRequest {

      String stripeToken;
      String email;
      PackageType packageType;
      String username;
      long numberOfLicense;

}