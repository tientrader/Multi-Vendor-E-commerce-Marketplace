package com.tien.order.kafka;

import com.tien.event.dto.PaymentResponse;
import com.tien.event.dto.NotificationEvent;
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
public class DLQFailureConsumer {

      KafkaProducer kafkaProducer;

      @KafkaListener(topics = "payment-response-dlq-failure")
      public void listenDlqFailure(PaymentResponse paymentResponse) {
            log.error("Message in DLQ-Failure: {}", paymentResponse);

            kafkaProducer.send("dlq-notification", NotificationEvent.builder()
                    .channel("email")
                    .recipient("facebooktnt123@gmail.com")
                    .subject("Critical Alert: DLQ-Failure")
                    .body("Message failed during DLQ retry process. Details: " + paymentResponse)
                    .build());
      }

}