package com.tien.payment.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stripe_charge")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeCharge {

      @Id
      String id;
      String stripeToken;
      String username;
      String email;
      Double amount;
      Boolean success;
      String chargeId;

}