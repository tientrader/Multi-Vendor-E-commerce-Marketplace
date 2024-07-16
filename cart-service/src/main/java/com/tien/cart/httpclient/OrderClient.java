package com.tien.cart.httpclient;

import com.tien.cart.configuration.AuthenticationRequestInterceptor;
import com.tien.cart.dto.request.OrderCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", url = "${app.services.order}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface OrderClient {

      @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
      void createOrder(@RequestBody OrderCreationRequest request);

}