package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", path = "/product", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @CircuitBreaker(name = "getProductPriceById")
      @Retry(name = "getProductPriceById")
      @GetMapping(value = "/{productId}/price/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Double> getProductPriceById(@PathVariable("productId") String productId,
                                              @PathVariable("variantId") String variantId);

      @CircuitBreaker(name = "getProductStockById")
      @Retry(name = "getProductStockById")
      @GetMapping(value = "/{productId}/stock/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<Integer> getProductStockById(@PathVariable("productId") String productId,
                                               @PathVariable("variantId") String variantId);

      @CircuitBreaker(name = "updateStockAndSoldQuantity")
      @Retry(name = "updateStockAndSoldQuantity")
      @PutMapping("/variants/{productId}/update-stock-sold")
      void updateStockAndSoldQuantity(@PathVariable String productId,
                                                   @RequestParam String variantId,
                                                   @RequestParam int quantity);

}