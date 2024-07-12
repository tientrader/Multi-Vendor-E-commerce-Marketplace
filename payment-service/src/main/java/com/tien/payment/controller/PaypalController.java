package com.tien.payment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.tien.payment.service.PaypalService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaypalController {

      PaypalService paypalService;

      @PostMapping
      public RedirectView createPayment(
              @RequestParam("method") String method,
              @RequestParam("amount") String amount,
              @RequestParam("currency") String currency,
              @RequestParam("description") String description
      ) {
            try {
                  Payment payment = paypalService.createPayment(
                          Double.valueOf(amount),
                          currency,
                          method,
                          "sale",
                          description,
                          "http://localhost:8085/payment/paypal/cancel",
                          "http://localhost:8085/payment/paypal/success");

                  for (Links links : payment.getLinks()) {
                        if (links.getRel().equals("approval_url")) {
                              return new RedirectView(links.getHref());
                        }
                  }

            } catch (PayPalRESTException e) {
                  log.error("Error occurred: " + e);
            }
            return new RedirectView("/payment/paypal/error");
      }

      @GetMapping("/success")
      public String success(@RequestParam("paymentId") Long paymentId, @RequestParam("PayerID") String payerId) {
            try {
                  Payment payment = paypalService.executePayment(paymentId, payerId);
                  if (payment.getState().equals("approved")) {
                        return "Paypal successful";
                  }
            } catch (PayPalRESTException e) {
                  log.error("Error occurred: " + e);
            }
            return "Paypal failed";
      }

      @GetMapping("/cancel")
      public String cancel() {
            return "Paypal canceled";
      }

      @GetMapping("/error")
      public String error() {
            return "Paypal error";
      }

}