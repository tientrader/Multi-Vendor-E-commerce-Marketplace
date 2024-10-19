package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VIPUserResponse {

      String stripeSubscriptionId;
      String username;
      String email;
      boolean vipStatus;
      LocalDate vipStartDate;
      LocalDate vipEndDate;

}