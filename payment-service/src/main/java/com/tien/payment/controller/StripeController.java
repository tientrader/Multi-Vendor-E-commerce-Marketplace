package com.tien.payment.controller;

import com.tien.payment.dto.ApiResponse;
import com.tien.payment.dto.request.*;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;
import com.tien.payment.service.StripeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/public/stripe")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeController {

      StripeService stripeService;

      @PostMapping("/charge")
      public ApiResponse<StripeChargeResponse> charge(@RequestBody StripeChargeRequest request) {
            return ApiResponse.<StripeChargeResponse>builder()
                    .message("Charge successful")
                    .result(stripeService.charge(request))
                    .build();
      }

      @PostMapping("/customer/subscription")
      public ApiResponse<StripeSubscriptionResponse> subscription(@RequestBody StripeSubscriptionRequest request) {
            return ApiResponse.<StripeSubscriptionResponse>builder()
                    .message("Subscription created successfully")
                    .result(stripeService.createSubscription(request))
                    .build();
      }

      @DeleteMapping("/subscription/{id}")
      public ApiResponse<SubscriptionCancelRecord> cancelSubscription(@PathVariable String id) {
            StripeSubscriptionResponse response = stripeService.cancelSubscription(id);
            return ApiResponse.<SubscriptionCancelRecord>builder()
                    .code(nonNull(response) ? 2000 : 4000)
                    .message(nonNull(response) ? "Subscription canceled successfully" : "Subscription cancellation failed")
                    .result(nonNull(response) ? new SubscriptionCancelRecord(response.getStatus()) : null)
                    .build();
      }

      @PostMapping("/session/payment")
      public ApiResponse<SessionResponse> sessionPayment(@RequestBody PaymentSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .message("Payment session created successfully")
                    .result(stripeService.createPaymentSession(request))
                    .build();
      }

      @PostMapping("/session/subscription")
      public ApiResponse<SessionResponse> createSubscriptionSession(@RequestBody SubscriptionSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .message("Subscription session created successfully")
                    .result(stripeService.createSubscriptionSession(request))
                    .build();
      }

}