package com.tien.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

      String stripeToken;
      String username;
      String email;
      Double amount;
      Boolean success;
      String chargeId;

}