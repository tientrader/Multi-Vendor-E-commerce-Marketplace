package com.tien.payment.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaypalRequest {

      @NotNull
      BigDecimal amount;

      @NotBlank
      String currency;

      @NotBlank
      String paymentMethod;

      @NotBlank
      String description;

      @NotBlank
      @URL
      String cancelUrl;

      @NotBlank
      @URL
      String successUrl;

}