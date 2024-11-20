package com.tien.review.httpclient;

import com.tien.review.configuration.AuthenticationRequestInterceptor;
import com.tien.review.dto.ApiResponse;
import com.tien.review.dto.response.ShopResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service", path = "/shop", configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

      @CircuitBreaker(name = "getShopByOwnerUsername")
      @Retry(name = "getShopByOwnerUsername")
      @GetMapping(value = "/owner/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ShopResponse> getShopByOwnerUsername(@PathVariable("username") String username);

}