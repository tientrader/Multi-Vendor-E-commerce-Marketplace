package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.request.OrderCreationRequest;
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

      @CircuitBreaker(name = "createOrder", fallbackMethod = "createOrderFallback")
      @Retry(name = "createOrder")
      @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Void> createOrder(@RequestBody OrderCreationRequest request);

      default ApiResponse<Void> createOrderFallback(OrderCreationRequest request, Throwable throwable) {
            return ApiResponse.<Void>builder()
                    .code(ErrorCode.ORDER_SERVICE_UNAVAILABLE.getCode())
                    .message("Order service is currently unavailable. Please try again later.")
                    .build();
      }

}