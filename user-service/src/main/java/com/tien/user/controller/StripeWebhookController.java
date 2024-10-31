package com.tien.user.controller;

import com.tien.user.dto.ApiResponse;
import com.tien.user.service.StripeWebhookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeWebhookController {

      StripeWebhookService stripeWebhookService;

      @PostMapping("/stripe")
      public ApiResponse<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
            stripeWebhookService.handleStripeEvent(payload, sigHeader);
            return ApiResponse.<String>builder()
                    .result("Event processed")
                    .build();
      }

}