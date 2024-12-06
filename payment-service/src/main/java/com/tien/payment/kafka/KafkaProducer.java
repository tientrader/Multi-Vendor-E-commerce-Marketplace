package com.tien.payment.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducer {

      KafkaTemplate<String, Object> kafkaTemplate;

      public void send(String topic, Object message) {
            try {
                  kafkaTemplate.send(topic, message);
                  log.info("Message sent to topic {}: {}", topic, message);
            } catch (Exception e) {
                  log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            }
      }

}