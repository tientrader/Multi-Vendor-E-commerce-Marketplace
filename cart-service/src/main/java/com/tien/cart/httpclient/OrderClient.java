package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.OrderCreationRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", url = "${app.services.order}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface OrderClient {

      @CircuitBreaker(name = "createOrderFromCart", fallbackMethod = "createOrderFromCartFallback")
      @Retry(name = "createOrder")
      @PostMapping(value = "/create-from-cart", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Void> createOrderFromCart(@RequestBody OrderCreationRequest request);

      default ApiResponse<Void> createOrderFromCartFallback(OrderCreationRequest request, Throwable throwable) {
            throw new RuntimeException();
      }

}