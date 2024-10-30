package com.tien.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentSessionRequest {

      String email;
      String username;
      BigDecimal amount;
      String productName;
      String currency;

}