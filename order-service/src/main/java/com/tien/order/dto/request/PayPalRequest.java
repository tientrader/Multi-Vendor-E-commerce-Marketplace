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

      @NotNull(message = "PAYPAL_AMOUNT_NULL")
      BigDecimal amount;

      @NotBlank(message = "PAYPAL_CURRENCY_BLANK")
      String currency;

      @NotBlank(message = "PAYPAL_PAYMENT_METHOD_BLANK")
      String paymentMethod;

      @NotBlank(message = "PAYPAL_DESCRIPTION_BLANK")
      String description;

      @NotBlank(message = "PAYPAL_CANCEL_URL_BLANK")
      @URL(message = "PAYPAL_CANCEL_URL_INVALID")
      String cancelUrl;

      @NotBlank(message = "PAYPAL_SUCCESS_URL_BLANK")
      @URL(message = "PAYPAL_SUCCESS_URL_INVALID")
      String successUrl;

}