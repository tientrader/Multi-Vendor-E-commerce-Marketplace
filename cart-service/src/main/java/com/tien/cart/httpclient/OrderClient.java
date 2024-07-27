package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.request.OrderCreationRequest;
import com.tien.cart.exception.AppException;
import com.tien.cart.exception.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", url = "${app.services.order}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface OrderClient {

      // Call the Order Service to create a new order
      @CircuitBreaker(name = "createOrder", fallbackMethod = "createOrderFallback")
      @Retry(name = "createOrder")
      @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
      void createOrder(@RequestBody OrderCreationRequest request);

      default void createOrderFallback() {
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
      }

}