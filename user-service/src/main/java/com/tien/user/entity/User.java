package com.tien.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_phone_number", columnList = "phone_number")
})
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

      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate dob;

      @Column(nullable = false)
      boolean vipStatus;

      LocalDate vipStartDate;
      LocalDate vipEndDate;
      String stripeSubscriptionId;

      @CreatedDate
      LocalDateTime createdAt;

      @LastModifiedDate
      LocalDateTime updatedAt;

}