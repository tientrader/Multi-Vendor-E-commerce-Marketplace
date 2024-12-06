package com.tien.payment.service;

import com.tien.payment.dto.request.PaymentSessionRequest;
import com.tien.event.dto.StripeChargeRequest;
import com.tien.payment.dto.request.StripeSubscriptionRequest;
import com.tien.payment.dto.request.SubscriptionSessionRequest;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;

import java.util.List;

public interface StripeService {

      StripeChargeResponse charge(StripeChargeRequest request);

      StripeChargeResponse processCharge(StripeChargeRequest request);

      StripeSubscriptionResponse createSubscription(StripeSubscriptionRequest request);

      SessionResponse createPaymentSession(PaymentSessionRequest request);

      SessionResponse createSubscriptionSession(SubscriptionSessionRequest request);

      void cancelSubscription(String subscriptionId);

      List<StripeSubscriptionResponse> retrieveAllSubscriptions();

}