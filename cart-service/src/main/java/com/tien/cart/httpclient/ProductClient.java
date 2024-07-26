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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      // Call the Product Service to get Product Price
      @CircuitBreaker(name = "getProductPriceById", fallbackMethod = "getProductPriceByIdFallback")
      @Retry(name = "getProductPriceById")
      @GetMapping(value = "/{productId}/price", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Double> getProductPriceById(@PathVariable("productId") String productId);

      // Call the Product Service to check if a product exists
      @CircuitBreaker(name = "existsProduct", fallbackMethod = "existsProductFallback")
      @Retry(name = "existsProduct")
      @GetMapping("/{productId}/exists")
      ExistsResponse existsProduct(@PathVariable String productId);

      default ApiResponse<Double> getProductPriceByIdFallback(String productId, Throwable t) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Product Service is unavailable", t);
      }

      default ExistsResponse existsProductFallback(String productId, Throwable t) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Product Service is unavailable", t);
      }

}