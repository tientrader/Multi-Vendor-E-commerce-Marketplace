package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import com.tien.order.httpclient.request.StripeChargeRequest;
import com.tien.order.httpclient.response.StripeChargeResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", path = "/payment", configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @CircuitBreaker(name = "chargePayment")
      @Retry(name = "chargePayment")
      @PostMapping("/stripe/charge")
      ApiResponse<StripeChargeResponse> charge(@RequestBody StripeChargeRequest request);

}