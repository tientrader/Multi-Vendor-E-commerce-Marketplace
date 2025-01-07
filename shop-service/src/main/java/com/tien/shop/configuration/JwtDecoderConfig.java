package com.tien.shop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class JwtDecoderConfig {

      @Value("${keycloak.url}")
      private String keycloakUrl;

      @Bean
      public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withJwkSetUri(keycloakUrl).build();
      }

}