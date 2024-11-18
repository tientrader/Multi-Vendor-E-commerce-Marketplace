package com.tien.review.httpclient;

import com.tien.review.configuration.AuthenticationRequestInterceptor;
import com.tien.review.dto.ApiResponse;
import com.tien.review.dto.response.ExistsResponse;
import com.tien.review.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path = "/product", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @GetMapping(value = "/{productId}/exists/{variantId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ExistsResponse> existsProduct(@PathVariable("productId") String productId,
                                                @PathVariable("variantId") String variantId);

      @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ProductResponse> getProductById(@PathVariable String productId);

}