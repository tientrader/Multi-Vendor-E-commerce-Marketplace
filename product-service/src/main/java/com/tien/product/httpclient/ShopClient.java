package com.tien.product.httpclient;

import com.tien.product.configuration.AuthenticationRequestInterceptor;
import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.response.ShopResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${app.services.identity}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ShopClient {

      @GetMapping(value = "/shop/owner/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
      ApiResponse<ShopResponse> getShopByOwnerUsername(@PathVariable("username") String username);

}