package com.tien.order.kafka;

import com.tien.event.dto.PaymentResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DLQRetryService {

      KafkaTemplate<String, PaymentResponse> kafkaTemplate;

      @Scheduled(fixedRate = 60000)
      public void retryDlqMessages() {
            try {
                  List<PaymentResponse> dlqMessages = fetchMessagesFromDlq();

                  for (PaymentResponse message : dlqMessages) {
                        try {
                              kafkaTemplate.send("payment-response", message);
                        } catch (Exception e) {
                              log.error("Retry failed for message: {}", message, e);
                              try {
                                    kafkaTemplate.send("payment-response-dlq-failure", message);
                              } catch (Exception failureEx) {
                                    log.error("Failed to move message to payment-response-dlq-failure: {}", message, failureEx);
                              }
                        }
                  }
            } catch (Exception e) {
                  log.error("Error during DLQ retry process", e);
            }
      }

      private List<PaymentResponse> fetchMessagesFromDlq() {
            List<PaymentResponse> messages = new ArrayList<>();
            Properties props = getProperties();

            try (KafkaConsumer<String, PaymentResponse> consumer = new KafkaConsumer<>(props)) {
                  consumer.subscribe(Collections.singletonList("payment-response-dlq"));
                  ConsumerRecords<String, PaymentResponse> records = consumer.poll(Duration.ofSeconds(5));
                  for (ConsumerRecord<String, PaymentResponse> record : records) {
                        messages.add(record.value());
                  }
            } catch (Exception e) {
                  log.error("Failed to fetch messages from DLQ", e);
            }

            return messages;
      }

      private static Properties getProperties() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9094");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
            props.put("spring.json.trusted.packages", "com.tien.event.dto");
            props.put("spring.json.value.default.type", PaymentResponse.class.getName());
            props.put("auto.offset.reset", "earliest");
            props.put("group.id", "dlq-retry-group");
            return props;
      }

}