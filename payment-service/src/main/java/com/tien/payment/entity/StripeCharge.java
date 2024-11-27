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
public class StripeCharge {

      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      String id;

      @Column(nullable = false)
      String username;

      @Column(nullable = false)
      String email;

      @Column(nullable = false)
      Double amount;

      @Column(nullable = false)
      Boolean success;

      @Column(nullable = false)
      String chargeId;

}