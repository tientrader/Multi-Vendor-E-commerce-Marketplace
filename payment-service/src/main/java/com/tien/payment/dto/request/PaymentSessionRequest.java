package com.tien.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentSessionRequest {

      @NotNull(message = "AMOUNT_IS_REQUIRED")
      @Positive(message = "AMOUNT_MUST_BE_POSITIVE")
      BigDecimal amount;

      @NotNull(message = "PRODUCT_NAME_IS_REQUIRED")
      String productName;

}