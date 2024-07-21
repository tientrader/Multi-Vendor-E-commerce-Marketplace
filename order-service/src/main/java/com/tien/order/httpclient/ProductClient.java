package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

    // Call the Product Service to get Product Price
    @GetMapping(value = "/{productId}/price", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<Double> getProductPriceById(@PathVariable("productId") String productId);

    // Call the Product Service to update stock
    @PutMapping("/{productId}/stock")
    ApiResponse<Void> updateStock(@PathVariable String productId, @RequestParam int quantity);

}