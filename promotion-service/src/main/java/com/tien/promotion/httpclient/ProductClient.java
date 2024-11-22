package com.tien.promotion.httpclient;

import com.tien.promotion.configuration.AuthenticationRequestInterceptor;
import com.tien.promotion.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/product", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @CircuitBreaker(name = "isProductExist")
      @Retry(name = "isProductExist")
      @GetMapping(value = "/{productId}/exists", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Boolean> isProductExist(@PathVariable String productId);

      @CircuitBreaker(name = "getShopIdByProductId")
      @Retry(name = "getShopIdByProductId")
      @GetMapping(value = "/{productId}/shopId", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<String> getShopIdByProductId(@PathVariable("productId") String productId);

}