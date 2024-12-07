package com.tien.payment.kafka;

import com.tien.event.dto.StripeChargeRequest;
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

      KafkaTemplate<String, StripeChargeRequest> kafkaTemplate;

      @Scheduled(fixedRate = 60000)
      public void retryDlqMessages() {
            try {
                  List<StripeChargeRequest> dlqMessages = fetchMessagesFromDlq();

                  for (StripeChargeRequest message : dlqMessages) {
                        try {
                              kafkaTemplate.send("payment-request", message);
                              log.info("Retried and sent to payment-request: {}", message);
                        } catch (Exception e) {
                              log.error("Retry failed for message: {}", message, e);
                              try {
                                    kafkaTemplate.send("payment-request-dlq-failure", message);
                              } catch (Exception failureEx) {
                                    log.error("Failed to move message to payment-request-dlq-failure: {}", message, failureEx);
                              }
                        }
                  }
            } catch (Exception e) {
                  log.error("Error during DLQ retry process", e);
            }
      }

      private List<StripeChargeRequest> fetchMessagesFromDlq() {
            List<StripeChargeRequest> messages = new ArrayList<>();
            Properties props = getProperties();

            try (KafkaConsumer<String, StripeChargeRequest> consumer = new KafkaConsumer<>(props)) {
                  consumer.subscribe(Collections.singletonList("payment-request-dlq"));
                  ConsumerRecords<String, StripeChargeRequest> records = consumer.poll(Duration.ofSeconds(5));
                  for (ConsumerRecord<String, StripeChargeRequest> record : records) {
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
            props.put("spring.json.value.default.type", StripeChargeRequest.class.getName());
            props.put("auto.offset.reset", "earliest");
            props.put("group.id", "dlq-retry-group");
            return props;
      }

}