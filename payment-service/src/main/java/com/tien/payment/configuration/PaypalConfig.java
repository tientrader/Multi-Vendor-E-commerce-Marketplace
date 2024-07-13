package com.tien.payment.configuration;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

      @Value("${paypal.client-id}")
      String clientId;

      @Value("${paypal.client-secret}")
      String clientSecret;

      @Value("${paypal.mode}")
      String mode;

      @Bean
      public APIContext apiContext() {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);
            apiContext.setConfigurationMap(getConfiguration());
            return apiContext;
      }

      private Map<String, String> getConfiguration() {
            Map<String, String> configMap = new HashMap<>();
            configMap.put("paypal.clientID", clientId);
            configMap.put("paypal.clientSecret", clientSecret);
            configMap.put("paypal.mode", mode);
            return configMap;
      }

}