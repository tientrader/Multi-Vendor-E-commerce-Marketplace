package com.tien.promotion.httpclient;

import com.tien.promotion.configuration.AuthenticationRequestInterceptor;
import com.tien.promotion.dto.ApiResponse;
import com.tien.promotion.dto.response.CartResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cart-service", path = "/cart", configuration = {AuthenticationRequestInterceptor.class})
public interface CartClient {

      @CircuitBreaker(name = "getMyCart")
      @Retry(name = "getMyCart")
      @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<CartResponse> getMyCart();

      @CircuitBreaker(name = "updateCartTotal")
      @Retry(name = "updateCartTotal")
      @PutMapping(value = "/update-total", produces = MediaType.APPLICATION_JSON_VALUE)
      void updateCartTotal(@RequestParam("username") String username, @RequestBody double total);

}