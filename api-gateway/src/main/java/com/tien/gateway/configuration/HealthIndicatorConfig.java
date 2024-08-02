package com.tien.gateway.configuration;

import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.boot.actuate.health.SimpleStatusAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthIndicatorConfig {

      @Bean
      public StatusAggregator statusAggregator() {
            return new SimpleStatusAggregator();
      }

}