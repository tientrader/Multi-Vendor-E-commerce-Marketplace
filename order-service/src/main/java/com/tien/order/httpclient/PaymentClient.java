package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.StripeChargeRequest;
import com.tien.order.dto.response.StripeChargeResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${app.services.payment}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @CircuitBreaker(name = "chargePayment", fallbackMethod = "chargeFallback")
      @Retry(name = "chargePayment")
      @PostMapping("/stripe/charge")
      ApiResponse<StripeChargeResponse> charge(@RequestBody StripeChargeRequest request);

      default ApiResponse<StripeChargeResponse> chargeFallback(StripeChargeRequest request, Throwable throwable) {
            throw new RuntimeException();
      }

}