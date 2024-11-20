package com.tien.review.httpclient;

import com.tien.review.configuration.AuthenticationRequestInterceptor;
import com.tien.review.dto.ApiResponse;
import com.tien.review.dto.response.ExistsResponse;
import com.tien.review.dto.response.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/product", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @CircuitBreaker(name = "existsProduct")
      @Retry(name = "existsProduct")
      @GetMapping(value = "/{productId}/exists/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ExistsResponse> existsProduct(@PathVariable("productId") String productId,
                                                @PathVariable("variantId") String variantId);

      @CircuitBreaker(name = "getProductById")
      @Retry(name = "getProductById")
      @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ProductResponse> getProductById(@PathVariable String productId);

}