package com.tien.gateway.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

      ReactiveJwtDecoder jwtDecoder;

      String[] publicEndpoints = {
              "/actuator/**",
              "/user/auth/register",
              "/user/auth/login-code",
              "/user/auth/login-password",
              "/user/auth/forgot-password",
              "/user/webhook/stripe"
      };

      @Value("${app.api-prefix}")
      @NonFinal
      String apiPrefix;

      @Override
      public int getOrder() {
            return -1;
      }

      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();

            response.getHeaders().add(
                    "Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains");

            if (isPublicEndpoint(request)) {
                  return chain.filter(exchange);
            }

            List<String> authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (CollectionUtils.isEmpty(authHeader)) {
                  return unauthenticated(response);
            }

            String token = authHeader.getFirst().replace("Bearer ", "");

            return jwtDecoder.decode(token)
                    .flatMap(jwt -> chain.filter(exchange))
                    .onErrorResume(throwable -> {
                          log.error("Authentication error: {}", throwable.getMessage());
                          if (throwable instanceof JwtException) {
                                return unauthenticated(response);
                          } else {
                                return serviceUnavailable(response);
                          }
                    });
      }

      private boolean isPublicEndpoint(ServerHttpRequest request) {
            return Arrays.stream(publicEndpoints).anyMatch(
                    s -> request.getURI().getPath().startsWith(apiPrefix + s)
            );
      }

      private Mono<Void> unauthenticated(ServerHttpResponse response) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Mono.just(response.bufferFactory().wrap("{\"error\":\"Unauthorized\"}".getBytes())));
      }

      private Mono<Void> serviceUnavailable(ServerHttpResponse response) {
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Mono.just(response.bufferFactory().wrap("{\"error\":\"Service Unavailable\"}".getBytes())));
      }

}