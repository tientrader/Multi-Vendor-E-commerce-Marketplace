package com.tien.payment.configuration;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

      @Bean
      public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
            FixedBackOff fixedBackOff = new FixedBackOff(5000L, 3);
            DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                    (record, ex) -> new TopicPartition(record.topic(), record.partition())
            );
            return new DefaultErrorHandler(recoverer, fixedBackOff);
      }

}