package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.ApiResponse;
import com.tien.cart.httpclient.response.ExistsResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

      @CircuitBreaker(name = "existsProduct")
      @Retry(name = "existsProduct")
      @GetMapping("/{productId}/exists/{variantId}")
      ApiResponse<ExistsResponse> existsProduct(@PathVariable String productId, @PathVariable String variantId);

      @CircuitBreaker(name = "getShopIdByProductId")
      @Retry(name = "getShopIdByProductId")
      @GetMapping(value = "/{productId}/shopId", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<String> getShopIdByProductId(@PathVariable("productId") String productId);

}