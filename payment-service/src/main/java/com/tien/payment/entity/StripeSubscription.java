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
public class StripeSubscription {

      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      String id;

      @Column(nullable = false)
      String stripeCustomerId;

      @Column(nullable = false)
      String stripeSubscriptionId;

      @Column(nullable = false)
      String stripePaymentMethodId;

      @Column(nullable = false)
      String username;

      @Column(nullable = false)
      String priceId;

      @Column(nullable = false)
      long numberOfLicense;

}