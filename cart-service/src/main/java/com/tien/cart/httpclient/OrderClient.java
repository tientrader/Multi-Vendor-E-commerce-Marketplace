package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.httpclient.request.OrderCreationRequest;
import com.tien.cart.httpclient.response.OrderResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", path = "/order", configuration = {AuthenticationRequestInterceptor.class})
public interface OrderClient {

      @CircuitBreaker(name = "createOrder")
      @Retry(name = "createOrder")
      @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<OrderResponse> createOrder(@RequestBody OrderCreationRequest request);

}