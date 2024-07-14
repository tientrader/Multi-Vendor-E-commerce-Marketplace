package com.tien.payment.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.tien.payment.dto.ApiResponse;
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
      public ApiResponse<PaymentIntent> createPaymentIntent(
              @RequestParam("amount") int amount,
              @RequestParam("currency") String currency,
              @RequestParam("description") String description
      ) throws StripeException {
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency, description);
            return ApiResponse.<PaymentIntent>builder()
                    .code(1000)
                    .message("Payment Intent created successfully")
                    .result(paymentIntent)
                    .build();
      }

      @PostMapping("/charge")
      public ApiResponse<String> chargeCustomer(
              @RequestParam("customerId") String customerId,
              @RequestParam("amount") int amount
      ) throws StripeException {
            String charge = stripeService.charge(customerId, amount).toJson();
            return ApiResponse.<String>builder()
                    .code(1000)
                    .message("Customer charged successfully")
                    .result(charge)
                    .build();
      }

      @PostMapping("/refund")
      public ApiResponse<String> refundCharge(
              @RequestParam("chargeId") String chargeId
      ) throws StripeException {
            String refund = stripeService.refund(chargeId).toJson();
            return ApiResponse.<String>builder()
                    .code(1000)
                    .message("Charge refunded successfully")
                    .result(refund)
                    .build();
      }

      @PostMapping("/customer")
      public ApiResponse<String> createCustomer(
              @RequestParam("email") String email,
              @RequestParam("source") String source
      ) throws StripeException {
            String customer = stripeService.createCustomer(email, source).toJson();
            return ApiResponse.<String>builder()
                    .code(1000)
                    .message("Customer created successfully")
                    .result(customer)
                    .build();
      }

}