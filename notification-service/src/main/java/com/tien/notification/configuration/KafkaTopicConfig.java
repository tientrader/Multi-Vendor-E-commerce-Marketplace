package com.tien.notification.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

      @Bean
      public NewTopic topicOne() {
            return TopicBuilder.name("register-successful")
                    .partitions(5)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicTwo() {
            return TopicBuilder.name("payment-successful")
                    .partitions(5)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicThree() {
            return TopicBuilder.name("order-created-successful")
                    .partitions(5)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicFour() {
            return TopicBuilder.name("shop-created-successful")
                    .partitions(3)
                    .replicas(1)
                    .build();
      }

      @Bean
      public NewTopic topicFive() {
            return TopicBuilder.name("dlq-notification")
                    .partitions(3)
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