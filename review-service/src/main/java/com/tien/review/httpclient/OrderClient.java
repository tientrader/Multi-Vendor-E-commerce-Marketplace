package com.tien.review.httpclient;

import com.tien.review.configuration.AuthenticationRequestInterceptor;
import com.tien.review.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "order-service", path = "/order", configuration = {AuthenticationRequestInterceptor.class})
public interface OrderClient {

      @CircuitBreaker(name = "getMyOrders")
      @Retry(name = "getMyOrders")
      @GetMapping(value = "/my-orders", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<List<?>> getMyOrders();

}