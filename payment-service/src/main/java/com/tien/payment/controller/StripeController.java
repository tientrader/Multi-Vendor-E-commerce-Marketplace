package com.tien.payment.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.tien.payment.service.StripeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripeController {

      StripeService stripeService;

      @PostMapping("/payment-intent")
      public PaymentIntent createPaymentIntent(
              @RequestParam("amount") int amount,
              @RequestParam("currency") String currency,
              @RequestParam("description") String description
      ) throws StripeException {
            return stripeService.createPaymentIntent(amount, currency, description);
      }

      @PostMapping("/charge")
      public String chargeCustomer(
              @RequestParam("customerId") String customerId,
              @RequestParam("amount") int amount
      ) throws StripeException {
            return stripeService.charge(customerId, amount).toJson();
      }

      @PostMapping("/refund")
      public String refundCharge(
              @RequestParam("chargeId") String chargeId
      ) throws StripeException {
            return stripeService.refund(chargeId).toJson();
      }

      @PostMapping("/customer")
      public String createCustomer(
              @RequestParam("email") String email,
              @RequestParam("source") String source
      ) throws StripeException {
            return stripeService.createCustomer(email, source).toJson();
      }

}