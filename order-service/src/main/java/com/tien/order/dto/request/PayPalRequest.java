package com.tien.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayPalRequest {

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