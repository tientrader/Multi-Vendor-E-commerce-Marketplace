package com.tien.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@Configuration
public class JwtDecoderConfig {

      @Bean
      public ReactiveJwtDecoder jwtDecoder() {
            return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:8180/realms/tienproapp/protocol/openid-connect/certs").build();
      }

}