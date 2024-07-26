package com.tien.product.httpclient;

import com.tien.product.configuration.AuthenticationRequestInterceptor;
import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.exception.AppException;
import com.tien.product.exception.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${app.services.identity}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

      // Get username to check the owner
      @CircuitBreaker(name = "getShopByOwnerUsername", fallbackMethod = "getShopByOwnerUsernameFallback")
      @Retry(name = "getShopByOwnerUsername")
      @GetMapping(value = "/shop/owner/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ShopResponse> getShopByOwnerUsername(@PathVariable("username") String username);

      default ApiResponse<ShopResponse> getShopByOwnerUsernameFallback() {
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
      }

}