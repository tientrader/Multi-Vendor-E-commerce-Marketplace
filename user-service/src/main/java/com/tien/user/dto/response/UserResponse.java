package com.tien.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

      String profileId;
      String email;
      String phoneNumber;
      String username;
      String firstName;
      String lastName;
      LocalDate dob;
      boolean vipStatus;
      LocalDate vipStartDate;
      LocalDate vipEndDate;
      LocalDateTime createdAt;
      LocalDateTime updatedAt;

}