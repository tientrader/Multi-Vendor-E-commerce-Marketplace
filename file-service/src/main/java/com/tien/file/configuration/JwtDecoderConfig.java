package com.tien.file.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

      @Bean
      public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/tienproapp/protocol/openid-connect/certs").build();
      }

}