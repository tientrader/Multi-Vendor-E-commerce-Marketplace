package com.tien.payment.kafka;

import com.tien.event.dto.NotificationEvent;
import com.tien.event.dto.StripeChargeRequest;
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
public class DLQConsumer {

      KafkaProducer kafkaProducer;

      @KafkaListener(topics = "payment-request-dlq")
      public void listenDlq(StripeChargeRequest stripeChargeRequest) {
            log.error("Message in DLQ: {}", stripeChargeRequest);

            kafkaProducer.send("dlq-notification", NotificationEvent.builder()
                    .channel("email")
                    .recipient("facebooktnt123@gmail.com")
                    .subject("DLQ Alert - Payment Service Issue")
                    .body("There was an issue processing payment-request. Please check the payment-service logs for details.")
                    .build());
      }

}