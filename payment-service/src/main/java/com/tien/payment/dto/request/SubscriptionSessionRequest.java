package com.tien.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionSessionRequest {

      String userId;
      String email;
      String username;
      Map<String, String> data;

}