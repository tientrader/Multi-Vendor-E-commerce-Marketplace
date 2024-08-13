package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import com.tien.order.exception.AppException;
import com.tien.order.exception.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

    @CircuitBreaker(name = "getProductPriceById", fallbackMethod = "getProductPriceByIdFallback")
    @Retry(name = "getProductPriceById")
    @GetMapping(value = "/{productId}/price", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<Double> getProductPriceById(@PathVariable("productId") String productId);

    @CircuitBreaker(name = "updateStock", fallbackMethod = "updateStockFallback")
    @Retry(name = "updateStock")
    @PutMapping("/{productId}/stock")
    ApiResponse<Void> updateStock(@PathVariable String productId, @RequestParam int quantity);

    default ApiResponse<Double> getProductPriceByIdFallback(String productId, Throwable throwable) {
        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    default ApiResponse<Void> updateStockFallback(String productId, int quantity, Throwable throwable) {
        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
    }

}