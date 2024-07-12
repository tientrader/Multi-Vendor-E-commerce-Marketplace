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
public class Paypal {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long paymentId;

      String payerId;
      double amount;
      String currency;
      String paymentMethod;
      String paymentState;
      String description;

}