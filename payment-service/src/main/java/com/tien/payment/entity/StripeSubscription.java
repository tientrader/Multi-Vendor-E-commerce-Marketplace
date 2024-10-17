package com.tien.payment.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stripe_subscription")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeSubscription {

      @Id
      String id;
      String stripeCustomerId;
      String stripeSubscriptionId;
      String stripePaymentMethodId;
      String username;
      String priceId;
      long numberOfLicense;

}