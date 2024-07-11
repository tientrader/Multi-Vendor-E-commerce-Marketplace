package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ProductResponse getProductById(@PathVariable("productId") String productId);

    @PutMapping(value = "/{productId}/stock")
    void updateStock(@PathVariable("productId") String productId, @RequestParam("quantity") int quantity);

}