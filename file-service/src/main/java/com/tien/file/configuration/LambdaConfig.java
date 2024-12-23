package com.tien.file.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Configuration
public class LambdaConfig {

      @Bean
      public LambdaClient lambdaClient() {
            return LambdaClient.create();
      }

}