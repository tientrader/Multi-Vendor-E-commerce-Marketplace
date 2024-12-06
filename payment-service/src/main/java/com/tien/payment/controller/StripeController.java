package com.tien.payment.controller;

import com.tien.event.dto.StripeChargeRequest;
import com.tien.payment.dto.ApiResponse;
import com.tien.payment.dto.request.*;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;
import com.tien.payment.service.StripeService;
import jakarta.validation.Valid;
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
      public ApiResponse<StripeChargeResponse> charge(@RequestBody @Valid StripeChargeRequest request) {
            return ApiResponse.<StripeChargeResponse>builder()
                    .result(stripeService.charge(request))
                    .build();
      }

      @PostMapping("/customer/subscription")
      public ApiResponse<StripeSubscriptionResponse> subscription(@RequestBody @Valid StripeSubscriptionRequest request) {
            return ApiResponse.<StripeSubscriptionResponse>builder()
                    .result(stripeService.createSubscription(request))
                    .build();
      }

      @PostMapping("/session/payment")
      public ApiResponse<SessionResponse> sessionPayment(@RequestBody @Valid PaymentSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .result(stripeService.createPaymentSession(request))
                    .build();
      }

      @PostMapping("/session/subscription")
      public ApiResponse<SessionResponse> createSubscriptionSession(@RequestBody @Valid SubscriptionSessionRequest request) {
            return ApiResponse.<SessionResponse>builder()
                    .result(stripeService.createSubscriptionSession(request))
                    .build();
      }

      @DeleteMapping("/subscription/{id}")
      public ApiResponse<Void> cancelSubscription(@PathVariable String id) {
            stripeService.cancelSubscription(id);
            return ApiResponse.<Void>builder()
                    .message("The subscription has been successfully canceled!")
                    .build();
      }

      @GetMapping("/subscriptions")
      public ApiResponse<List<StripeSubscriptionResponse>> retrieveAllSubscriptions() {
            return ApiResponse.<List<StripeSubscriptionResponse>>builder()
                    .result(stripeService.retrieveAllSubscriptions())
                    .build();
      }

}