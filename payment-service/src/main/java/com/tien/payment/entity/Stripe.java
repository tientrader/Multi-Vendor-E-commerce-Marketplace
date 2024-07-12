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
public class Stripe {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long id;

      String chargeId;
      String customerId;
      int amount;
      String currency;
      String description;
      String status;

}