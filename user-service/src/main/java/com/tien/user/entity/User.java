package com.tien.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {

      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      String profileId;

      @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
      String email;

      @Column(name = "phone_number", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
      String phoneNumber;

      @Column(name = "username", nullable = false, unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
      String username;

      @Column(nullable = false)
      String userId;

      @Column(nullable = false)
      String firstName;

      @Column(nullable = false)
      String lastName;

      LocalDate dob;

      @Column(nullable = false)
      boolean vipStatus;

      LocalDate vipStartDate;
      LocalDate vipEndDate;
      String stripeSubscriptionId;

}