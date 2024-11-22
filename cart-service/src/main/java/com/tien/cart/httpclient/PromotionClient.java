package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "promotion-service", path = "/promotion", configuration = {AuthenticationRequestInterceptor.class})
public interface PromotionClient {

      @CircuitBreaker(name = "applyPromotionCode")
      @Retry(name = "applyPromotionCode")
      @PostMapping(value = "/apply", produces = MediaType.APPLICATION_JSON_VALUE)
      void applyPromotionCode(@RequestParam String promoCode);

}