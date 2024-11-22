package com.tien.promotion.httpclient;

import com.tien.promotion.configuration.AuthenticationRequestInterceptor;
import com.tien.promotion.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service", path = "/shop", configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

      @CircuitBreaker(name = "checkIfShopExists")
      @Retry(name = "checkIfShopExists")
      @GetMapping(value = "/{shopId}/exists", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Boolean> checkIfShopExists(@PathVariable("shopId") String shopId);

}