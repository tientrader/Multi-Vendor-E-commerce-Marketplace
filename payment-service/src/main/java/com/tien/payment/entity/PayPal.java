package com.tien.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PayPal {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long paymentId;

      String payerId;
      BigDecimal amount;
      String currency;
      String paymentMethod;
      String paymentState;
      String description;

}