package com.tien.payment.mapper;

import com.tien.payment.dto.request.*;
import com.tien.payment.dto.response.*;
import com.tien.payment.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StripeMapper {

      @Mapping(target = "success", ignore = true)
      @Mapping(target = "id", ignore = true)
      @Mapping(target = "chargeId", ignore = true)
      StripeCharge toStripeCharge(StripeChargeRequest request);

      StripeChargeResponse toStripeChargeResponse(StripeCharge stripeCharge);

      @Mapping(target = "stripeSubscriptionId", ignore = true)
      @Mapping(target = "stripePaymentMethodId", ignore = true)
      @Mapping(target = "stripeCustomerId", ignore = true)
      @Mapping(target = "id", ignore = true)
      StripeSubscription toStripeSubscription(StripeSubscriptionRequest request);

      @Mapping(target = "message", ignore = true)
      @Mapping(target = "status", ignore = true)
      StripeSubscriptionResponse toStripeSubscriptionResponse(StripeSubscription stripeSubscription);

      @Mapping(target = "sessionUrl", ignore = true)
      @Mapping(target = "sessionId", ignore = true)
      Session toSession(PaymentSessionRequest request);

      SessionResponse toSessionResponse(Session session);

}