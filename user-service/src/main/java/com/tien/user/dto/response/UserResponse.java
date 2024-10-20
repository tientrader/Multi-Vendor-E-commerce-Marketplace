package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

      String profileId;
      String email;
      String username;
      String firstName;
      String lastName;

      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate dob;

      boolean vipStatus;
      LocalDate vipStartDate;
      LocalDate vipEndDate;
      String stripeSubscriptionId;

}