package com.tien.user.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.service.StripeWebhookService;
import com.tien.user.service.VIPUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeWebhookServiceImpl implements StripeWebhookService {

      VIPUserService vipUserService;

      @Value("${stripe.endpoint.secret}")
      @NonFinal
      String endpointSecret;

      @Override
      public void handleStripeEvent(String payload, String sigHeader) {
            try {
                  Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

                  switch (event.getType()) {
                        case "checkout.session.completed" -> {
                              Session session = (Session) event.getData().getObject();
                              String username = session.getMetadata().get("username");
                              String packageType = session.getMetadata().get("packageType");
                              String subscriptionId = session.getSubscription();

                              vipUserService.updateVipEndDate(username, packageType, subscriptionId);
                        }
                        case "customer.subscription.created" -> {
                              com.stripe.model.Subscription subscription = (com.stripe.model.Subscription) event.getData().getObject();
                              String subscriptionId = subscription.getId();
                              String username = subscription.getMetadata().get("username");
                              String packageType = subscription.getMetadata().get("packageType");

                              vipUserService.updateVipEndDate(username, packageType, subscriptionId);
                        }
                  }
            } catch (SignatureVerificationException e) {
                  log.error("Invalid signature: {}", e.getMessage());
                  throw new AppException(ErrorCode.INVALID_STRIPE_SIGNATURE);
            }
      }

}