package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.dto.response.ExistsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @CircuitBreaker(name = "getProductPriceById", fallbackMethod = "getProductPriceByIdFallback")
      @Retry(name = "getProductPriceById")
      @GetMapping(value = "/{productId}/price/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Double> getProductPriceById(@PathVariable("productId") String productId,
                                              @PathVariable("variantId") String variantId);

      @CircuitBreaker(name = "existsProduct", fallbackMethod = "existsProductFallback")
      @Retry(name = "existsProduct")
      @GetMapping("/{productId}/exists/{variantId}")
      ExistsResponse existsProduct(@PathVariable String productId, @PathVariable String variantId);

      default ApiResponse<Double> getProductPriceByIdFallback(String productId, String variantId, Throwable throwable) {
            throw new RuntimeException();
      }

      default ExistsResponse existsProductFallback(String productId, String variantId, Throwable throwable) {
            throw new RuntimeException();
      }

}