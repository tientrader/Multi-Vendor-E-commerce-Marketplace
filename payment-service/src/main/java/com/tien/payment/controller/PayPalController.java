package com.tien.payment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.tien.payment.dto.ApiResponse;
import com.tien.payment.dto.request.PayPalRequest;
import com.tien.payment.service.PayPalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PayPalController {

      PayPalService paypalService;

      @PostMapping("/create")
      public ApiResponse<String> createPayment(
              @Validated @RequestBody PayPalRequest request) {
            try {
                  Payment payment = paypalService.createPayment(request);

                  for (Links links : payment.getLinks()) {
                        if (links.getRel().equals("approval_url")) {
                              return ApiResponse.<String>builder()
                                      .code(1000)
                                      .message("Redirect URL generated successfully")
                                      .result(links.getHref())
                                      .build();
                        }
                  }
            } catch (PayPalRESTException e) {
                  log.error("Error occurred: " + e);
            }
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Failed to generate redirect URL")
                    .build();
      }

      @GetMapping("/success")
      public ApiResponse<String> success(
              @RequestParam("paymentId") Long paymentId,
              @RequestParam("PayerID") String payerId) {
            try {
                  Payment payment = paypalService.executePayment(paymentId, payerId);
                  if (payment.getState().equals("approved")) {
                        return ApiResponse.<String>builder()
                                .code(1000)
                                .message("Payment successful")
                                .result("Paypal successful")
                                .build();
                  }
            } catch (PayPalRESTException e) {
                  log.error("Error occurred: " + e);
            }
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Payment failed")
                    .build();
      }

      @GetMapping("/cancel")
      public ApiResponse<String> cancel() {
            return ApiResponse.<String>builder()
                    .code(1000)
                    .message("Paypal canceled")
                    .result("Paypal canceled")
                    .build();
      }

      @GetMapping("/error")
      public ApiResponse<String> error() {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Paypal error")
                    .build();
      }

}