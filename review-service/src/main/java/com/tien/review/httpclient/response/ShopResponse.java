package com.tien.review.httpclient.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopResponse {

      String id;
      String name;
      String email;
      String ownerUsername;

}