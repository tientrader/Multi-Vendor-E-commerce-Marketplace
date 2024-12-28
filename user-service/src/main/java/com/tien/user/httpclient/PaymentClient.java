package com.tien.user.httpclient;

import com.tien.user.configuration.AuthenticationRequestInterceptor;
import com.tien.user.dto.ApiResponse;
import com.tien.user.httpclient.request.SubscriptionSessionRequest;
import com.tien.user.httpclient.response.SessionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", path = "/payment", configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @CircuitBreaker(name = "createSubscriptionSession")
      @Retry(name = "createSubscriptionSession")
      @PostMapping("/stripe/session/subscription")
      ApiResponse<SessionResponse> createSubscriptionSession(@RequestBody SubscriptionSessionRequest request);

      @CircuitBreaker(name = "cancelSubscription")
      @Retry(name = "cancelSubscription")
      @DeleteMapping("/stripe/subscription/{id}")
      void cancelSubscription(@PathVariable("id") String id);

}