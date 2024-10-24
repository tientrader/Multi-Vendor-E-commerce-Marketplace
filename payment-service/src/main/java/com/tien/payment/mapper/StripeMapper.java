package com.tien.payment.mapper;

import com.tien.payment.dto.request.*;
import com.tien.payment.dto.response.*;
import com.tien.payment.entity.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StripeMapper {

      StripeCharge toStripeCharge(StripeChargeRequest request);

      StripeChargeResponse toStripeChargeResponse(StripeCharge stripeCharge);

      StripeSubscription toStripeSubscription(StripeSubscriptionRequest request);

      StripeSubscriptionResponse toStripeSubscriptionResponse(StripeSubscription stripeSubscription);

      Session toSession(PaymentSessionRequest request);

      SessionResponse toSessionResponse(Session session);

}