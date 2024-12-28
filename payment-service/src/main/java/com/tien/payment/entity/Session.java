package com.tien.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Session {

      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      String id;

      @Column(nullable = false, length = 400)
      String sessionUrl;

      @Column(nullable = false)
      String username;

}