package com.tien.payment.service;

import com.tien.payment.dto.request.*;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;

import java.util.List;

public interface StripeService {

      StripeChargeResponse charge(StripeChargeRequest request);
      StripeSubscriptionResponse createSubscription(StripeSubscriptionRequest request);
      SessionResponse createPaymentSession(PaymentSessionRequest request);
      SessionResponse createSubscriptionSession(SubscriptionSessionRequest request);
      StripeSubscriptionResponse cancelSubscription(String subscriptionId);
      List<StripeSubscriptionResponse> retrieveAllSubscriptions();

}