package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service", path = "/shop", configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

      @CircuitBreaker(name = "getOwnerUsernameByShopId")
      @Retry(name = "getOwnerUsernameByShopId")
      @GetMapping(value = "/{shopId}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<String> getOwnerUsernameByShopId(@PathVariable("shopId") String shopId);

}