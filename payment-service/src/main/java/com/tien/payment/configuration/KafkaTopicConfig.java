package com.tien.payment.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

      @Bean
      public NewTopic topicOne() {
            return TopicBuilder.name("payment-response")
                    .partitions(5)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicTwo() {
            return TopicBuilder.name("payment-response-dlq")
                    .partitions(3)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicThree() {
            return TopicBuilder.name("payment-response-dlq-failure")
                    .partitions(1)
                    .replicas(1)
                    .build();
      }

      @Bean
      public KafkaAdmin kafkaAdmin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
            return new KafkaAdmin(configs);
      }

}