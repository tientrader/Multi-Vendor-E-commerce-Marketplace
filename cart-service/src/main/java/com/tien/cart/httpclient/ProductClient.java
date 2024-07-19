package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.response.ExistsResponse;
import com.tien.cart.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      // Call the Product Service to get Product Information
      @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ProductResponse getProductById(@PathVariable("productId") String productId);

      // Call the Product Service to check if a product exists
      @GetMapping("/{productId}/exists")
      ExistsResponse existsProduct(@PathVariable String productId);

}