package com.tien.user.httpclient;

import com.tien.user.configuration.AuthenticationRequestInterceptor;
import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.StripeSubscriptionRequest;
import com.tien.user.dto.request.SubscriptionSessionRequest;
import com.tien.user.dto.response.SessionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", path = "/payment", configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @CircuitBreaker(name = "createSubscription", fallbackMethod = "createSubscriptionFallback")
      @Retry(name = "createSubscription")
      @PostMapping("/stripe/customer/subscription")
      void createSubscription(@RequestBody StripeSubscriptionRequest request);

      @CircuitBreaker(name = "cancelSubscription", fallbackMethod = "cancelSubscriptionFallback")
      @Retry(name = "cancelSubscription")
      @DeleteMapping("/stripe/subscription/{id}")
      void cancelSubscription(@PathVariable("id") String id);

      @CircuitBreaker(name = "createSubscriptionSession", fallbackMethod = "createSubscriptionSessionFallback")
      @Retry(name = "createSubscriptionSession")
      @PostMapping("/stripe/session/subscription")
      ApiResponse<SessionResponse> createSubscriptionSession(@RequestBody SubscriptionSessionRequest request);

      default void createSubscriptionFallback(StripeSubscriptionRequest request, Throwable throwable) {
            throw new RuntimeException();
      }

      default void cancelSubscriptionFallback(String id, Throwable throwable) {
            throw new RuntimeException();
      }

      default ApiResponse<SessionResponse> createSubscriptionSessionFallback(SubscriptionSessionRequest request, Throwable throwable) {
            throw new RuntimeException();
      }

}