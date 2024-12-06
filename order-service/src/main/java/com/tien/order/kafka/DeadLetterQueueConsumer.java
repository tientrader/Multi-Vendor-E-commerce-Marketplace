package com.tien.order.kafka;

import com.tien.event.dto.NotificationEvent;
import com.tien.event.dto.PaymentResponse;
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
public class DeadLetterQueueConsumer {

      KafkaProducer kafkaProducer;

      @KafkaListener(topics = "payment-response-dlq")
      public void listenDlq(PaymentResponse paymentResponse) {
            log.error("Message in DLQ: {}", paymentResponse);
            kafkaProducer.send("dlq-notification", NotificationEvent.builder()
                    .channel("email")
                    .recipient("facebooktnt123@gmail.com")
                    .subject("DLQ Alert - Order Service Issue")
                    .body("There was an issue processing payment-response. Please check the order-service logs for details.")
                    .build());
      }

}