package com.tien.payment.controller;

import com.tien.event.dto.StripeChargeRequest;
import com.tien.payment.service.StripeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentConsumer {

      StripeService stripeService;

      @KafkaListener(topics = "payment-request")
      public void listenPaymentRequest(StripeChargeRequest request) {
            log.info("Received payment request: {}", request);
            stripeService.processCharge(request);
      }

}