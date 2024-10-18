package com.tien.order.httpclient;

import com.tien.order.configuration.AuthenticationRequestInterceptor;
import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.StripeChargeRequest;
import com.tien.order.dto.response.StripeChargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "${app.services.payment}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @PostMapping("/stripe/charge")
      ApiResponse<StripeChargeResponse> charge(@RequestBody StripeChargeRequest request);

}