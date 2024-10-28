package com.tien.user.httpclient;

import com.tien.user.configuration.AuthenticationRequestInterceptor;
import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.StripeSubscriptionRequest;
import com.tien.user.dto.request.SubscriptionCancelRecord;
import com.tien.user.dto.response.StripeSubscriptionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", path = "/payment", configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @CircuitBreaker(name = "createSubscription", fallbackMethod = "createSubscriptionFallback")
      @Retry(name = "createSubscription")
      @PostMapping("/stripe/customer/subscription")
      ApiResponse<StripeSubscriptionResponse> createSubscription(@RequestBody StripeSubscriptionRequest request);

      @CircuitBreaker(name = "cancelSubscription", fallbackMethod = "cancelSubscriptionFallback")
      @Retry(name = "cancelSubscription")
      @DeleteMapping("/stripe/subscription/{id}")
      ApiResponse<SubscriptionCancelRecord> cancelSubscription(@PathVariable("id") String id);

      default ApiResponse<StripeSubscriptionResponse> createSubscriptionFallback(StripeSubscriptionRequest request, Throwable throwable) {
            throw new RuntimeException();
      }

      default ApiResponse<SubscriptionCancelRecord> cancelSubscriptionFallback(String id, Throwable throwable) {
            throw new RuntimeException();
      }

}