package com.tien.user.service;

public interface StripeWebhookService {

      void handleStripeEvent(String payload, String sigHeader);

}