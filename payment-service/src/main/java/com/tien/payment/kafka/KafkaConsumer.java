package com.tien.payment.kafka;

import com.tien.event.dto.StripeChargeRequest;
import com.tien.payment.service.StripeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumer {

      StripeService stripeService;
      KafkaProducer kafkaProducer;

      @KafkaListener(topics = "payment-request")
      public void listenPaymentRequest(StripeChargeRequest request) {
            try {
                  stripeService.processCharge(request);
            } catch (Exception e) {
                  log.error("Failed to process payment request: {}", request, e);
                  kafkaProducer.send("payment-request-dlq", request);
            }
      }

}