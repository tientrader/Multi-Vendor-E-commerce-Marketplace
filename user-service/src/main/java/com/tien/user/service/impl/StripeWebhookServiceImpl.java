package com.tien.user.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.tien.user.service.StripeWebhookService;
import com.tien.user.service.VIPUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookServiceImpl implements StripeWebhookService {

      private final VIPUserService vipUserService;

      @Value("${stripe.endpoint.secret}")
      private String endpointSecret;

      @Override
      public void handleStripeEvent(String payload, String sigHeader) {
            try {
                  Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
                  log.info("Event type: {}", event.getType());

                  if ("checkout.session.completed".equals(event.getType())) {
                        Session session = (Session) event.getData().getObject();
                        String username = session.getMetadata().get("username");
                        String packageType = session.getMetadata().get("packageType");

                        if (username != null && packageType != null) {
                              vipUserService.updateVipEndDate(username, packageType);
                        } else {
                              log.warn("Missing username or packageType");
                        }
                  } else {
                        log.warn("Unhandled event type: {}", event.getType());
                  }
            } catch (SignatureVerificationException e) {
                  log.error("Invalid signature: {}", e.getMessage());
            } catch (Exception e) {
                  log.error("Error processing event: {}", e.getMessage());
            }
      }

}