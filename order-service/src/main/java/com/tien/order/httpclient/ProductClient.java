package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

    @PutMapping("/{productId}/stock")
    void updateStock(@PathVariable("productId") String productId, @RequestParam("quantity") int quantity);

}