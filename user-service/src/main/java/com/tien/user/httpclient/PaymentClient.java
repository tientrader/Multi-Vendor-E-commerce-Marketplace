package com.tien.user.httpclient;

import com.tien.user.configuration.AuthenticationRequestInterceptor;
import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.StripeSubscriptionRequest;
import com.tien.user.dto.request.SubscriptionCancelRecord;
import com.tien.user.dto.response.StripeSubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "${app.services.payment}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface PaymentClient {

      @PostMapping("/stripe/customer/subscription")
      ApiResponse<StripeSubscriptionResponse> createSubscription(@RequestBody StripeSubscriptionRequest request);

      @DeleteMapping("/stripe/subscription/{id}")
      ApiResponse<SubscriptionCancelRecord> cancelSubscription(@PathVariable("id") String id);

      @GetMapping("/stripe/subscription/{id}")
      ApiResponse<StripeSubscriptionResponse> retrieveSubscriptionDetails(@PathVariable("id") String id);

}