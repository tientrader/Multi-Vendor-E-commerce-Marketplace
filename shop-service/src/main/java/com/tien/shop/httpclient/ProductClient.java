package com.tien.shop.httpclient;

import com.tien.shop.configuration.AuthenticationRequestInterceptor;
import com.tien.shop.dto.ApiResponse;
import com.tien.shop.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", path = "/product", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {

      @GetMapping("/shop/{shopId}")
      ApiResponse<List<ProductResponse>> getProductsByShopId(@PathVariable("shopId") String shopId);

}