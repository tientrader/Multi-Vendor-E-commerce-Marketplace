package com.tien.order.httpclient;

import com.tien.order.dto.ApiResponse;
import com.tien.order.dto.request.PayPalRequest;
import com.tien.order.dto.response.PayPalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${app.services.payment}")
public interface PaymentClient {

      @PostMapping("/paypal/create")
      ApiResponse<PayPalResponse> createPayment(@RequestBody PayPalRequest request);

}