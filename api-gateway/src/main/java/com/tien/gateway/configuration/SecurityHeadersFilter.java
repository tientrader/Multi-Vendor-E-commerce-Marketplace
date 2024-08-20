package com.tien.gateway.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityHeadersFilter implements GlobalFilter {

      static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
      static final String X_XSS_PROTECTION = "X-XSS-Protection";

      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpResponse response = exchange.getResponse();

            response.getHeaders().add(STRICT_TRANSPORT_SECURITY, "max-age=31536000; includeSubDomains");
            response.getHeaders().add(X_XSS_PROTECTION, "1; mode=block");

            return chain.filter(exchange);
      }

}