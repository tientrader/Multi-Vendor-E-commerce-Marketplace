package com.tien.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtDecoderConfig {

      @Value("${keycloak.url}")
      private String keycloakUrl;

      @Bean
      public ReactiveJwtDecoder jwtDecoder() {
            return NimbusReactiveJwtDecoder.withJwkSetUri(keycloakUrl).build();
      }

}