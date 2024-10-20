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

import java.util.List;

@RestController
@RequestMapping("/stripe")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeController {

      StripeService stripeService;

      @PostMapping("/charge")
      public ApiResponse<StripeChargeResponse> charge(@RequestBody StripeChargeRequest request) {
            return ApiResponse.<StripeChargeResponse>builder()
                    .result(stripeService.charge(request))
                    .build();
      }

      @PostMapping("/customer/subscription")
      public ApiResponse<StripeSubscriptionResponse> subscription(@RequestBody StripeSubscriptionRequest request) {
            return ApiResponse.<StripeSubscriptionResponse>builder()
                    .result(stripeService.createSubscription(request))
                    .build();
      }

      @PostMapping("/session/payment")
      public ApiResponse<SessionResponse> sessionPayment(@RequestBody PaymentSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .result(stripeService.createPaymentSession(request))
                    .build();
      }

      @PostMapping("/session/subscription")
      public ApiResponse<SessionResponse> createSubscriptionSession(@RequestBody SubscriptionSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .result(stripeService.createSubscriptionSession(request))
                    .build();
      }

      @DeleteMapping("/subscription/{id}")
      public ApiResponse<SubscriptionCancelRecord> cancelSubscription(@PathVariable String id) {
            return ApiResponse.<SubscriptionCancelRecord>builder()
                    .result(new SubscriptionCancelRecord(stripeService.cancelSubscription(id).getStatus()))
                    .build();
      }

      @GetMapping("/subscriptions")
      public ApiResponse<List<StripeSubscriptionResponse>> retrieveAllSubscriptions() {
            return ApiResponse.<List<StripeSubscriptionResponse>>builder()
                    .result(stripeService.retrieveAllSubscriptions())
                    .build();
      }

}