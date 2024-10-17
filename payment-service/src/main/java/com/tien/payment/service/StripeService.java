package com.tien.payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.tien.event.dto.NotificationEvent;
import com.tien.payment.dto.request.PaymentSessionRequest;
import com.tien.payment.dto.request.StripeChargeRequest;
import com.tien.payment.dto.request.StripeSubscriptionRequest;
import com.tien.payment.dto.request.SubscriptionSessionRequest;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;
import com.tien.payment.entity.StripeCharge;
import com.tien.payment.entity.StripeSubscription;
import com.tien.payment.mapper.StripeMapper;
import com.tien.payment.repository.SessionRepository;
import com.tien.payment.repository.StripeChargeRepository;
import com.tien.payment.repository.StripeSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeService {

      StripeChargeRepository stripeChargeRepository;
      StripeSubscriptionRepository stripeSubscriptionRepository;
      SessionRepository sessionRepository;
      StripeMapper stripeMapper;
      KafkaTemplate<String, Object> kafkaTemplate;

      @Value("${stripe.api.key}")
      @NonFinal
      String stripeApiKey;

      @PostConstruct
      public void init() {
            Stripe.apiKey = stripeApiKey;
      }

      public StripeChargeResponse charge(StripeChargeRequest request) {
            StripeCharge stripeCharge = stripeMapper.toStripeCharge(request);
            try {
                  Map<String, Object> chargeParams = new HashMap<>();
                  chargeParams.put("amount", (int) (request.getAmount() * 100));
                  chargeParams.put("currency", "USD");
                  chargeParams.put("description", "Payment for id " + request.getAdditionalInfo().getOrDefault("ID_TAG", ""));
                  chargeParams.put("source", request.getStripeToken());

                  HashMap<String, Object> metadata = new HashMap<>(request.getAdditionalInfo());
                  metadata.put("username", request.getUsername());
                  chargeParams.put("metadata", metadata);

                  Charge charge = Charge.create(chargeParams);
                  stripeCharge.setMessage(charge.getOutcome().getSellerMessage());

                  if (charge.getPaid()) {
                        stripeCharge.setChargeId(charge.getId());
                        stripeCharge.setSuccess(true);

                        kafkaTemplate.send("payment_successful", NotificationEvent.builder()
                                .channel("email")
                                .recipient(request.getEmail())
                                .subject("Payment Successful")
                                .body("Your payment of " + request.getAmount() + " USD was successful.")
                                .build());
                  } else {
                        stripeCharge.setSuccess(false);
                  }

                  stripeChargeRepository.save(stripeCharge);
                  return stripeMapper.toStripeChargeResponse(stripeCharge);
            } catch (StripeException e) {
                  log.error("StripeService (charge)", e);
                  throw new RuntimeException(e.getMessage());
            }
      }

      public StripeSubscriptionResponse createSubscription(StripeSubscriptionRequest request) {
            StripeSubscription stripeSubscription = stripeMapper.toStripeSubscription(request);
            try {
                  Map<String, Object> paymentMethodParams = new HashMap<>();
                  paymentMethodParams.put("type", "card");
                  paymentMethodParams.put("card", Map.of("token", request.getStripeToken()));
                  PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

                  Map<String, Object> customerMap = new HashMap<>();
                  customerMap.put("name", request.getUsername());
                  customerMap.put("email", request.getEmail());
                  customerMap.put("payment_method", paymentMethod.getId());
                  Customer customer = Customer.create(customerMap);

                  paymentMethod = PaymentMethod.retrieve(paymentMethod.getId());
                  paymentMethod = paymentMethod.attach(Map.of("customer", customer.getId()));

                  List<Object> items = new ArrayList<>();
                  items.add(Map.of("price", request.getPriceId(), "quantity", request.getNumberOfLicense()));

                  Subscription subscription = Subscription.create(Map.of(
                          "customer", customer.getId(),
                          "default_payment_method", paymentMethod.getId(),
                          "items", items
                  ));

                  stripeSubscription.setStripeCustomerId(customer.getId());
                  stripeSubscription.setStripeSubscriptionId(subscription.getId());
                  stripeSubscription.setStripePaymentMethodId(paymentMethod.getId());
                  stripeSubscription.setUsername(request.getUsername());

                  stripeSubscriptionRepository.save(stripeSubscription);

                  kafkaTemplate.send("payment_successful", NotificationEvent.builder()
                          .channel("email")
                          .recipient(request.getEmail())
                          .subject("Subscription Created")
                          .body("Your subscription has been created successfully.")
                          .build());

                  return stripeMapper.toStripeSubscriptionResponse(stripeSubscription);
            } catch (StripeException e) {
                  log.error("StripeService (createSubscription)", e);
                  throw new RuntimeException(e.getMessage());
            }
      }

      public SessionResponse createPaymentSession(PaymentSessionRequest request) {
            com.tien.payment.entity.Session paymentSession = stripeMapper.toSession(request);
            try {
                  BigDecimal amount = request.getAmount();
                  String productName = request.getProductName();

                  String currency = request.getCurrency() != null ? request.getCurrency() : "USD";

                  Customer customer = findOrCreateCustomer(request.getEmail(), request.getUsername());
                  String clientUrl = "https://localhost:4200";

                  SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                          .setMode(SessionCreateParams.Mode.PAYMENT)
                          .setCustomer(customer.getId())
                          .setSuccessUrl(clientUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                          .setCancelUrl(clientUrl + "/failure");

                  sessionCreateParamsBuilder.addLineItem(
                          SessionCreateParams.LineItem.builder()
                                  .setQuantity(1L)
                                  .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                          .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                  .putMetadata("cart_id", "123")
                                                  .putMetadata("user_id", request.getUserId())
                                                  .setName(productName)
                                                  .build())
                                          .setCurrency(currency)
                                          .setUnitAmountDecimal(amount.multiply(BigDecimal.valueOf(100)))
                                          .build())
                                  .build());

                  SessionCreateParams.PaymentIntentData paymentIntentData =
                          SessionCreateParams.PaymentIntentData.builder()
                                  .putMetadata("cart_id", "123")
                                  .putMetadata("user_id", request.getUserId())
                                  .build();
                  sessionCreateParamsBuilder.setPaymentIntentData(paymentIntentData);

                  com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(sessionCreateParamsBuilder.build());

                  paymentSession.setSessionUrl(stripeSession.getUrl());
                  paymentSession.setSessionId(stripeSession.getId());
                  sessionRepository.save(paymentSession);

                  kafkaTemplate.send("payment_successful", NotificationEvent.builder()
                          .channel("email")
                          .recipient(request.getEmail())
                          .subject("Payment Session Created")
                          .body("Your payment session has been created successfully.")
                          .build());

                  return stripeMapper.toSessionResponse(paymentSession);
            } catch (StripeException e) {
                  log.error("Error creating payment session: {}", e.getMessage());
                  throw new RuntimeException("Failed to create payment session: " + e.getMessage());
            } catch (Exception e) {
                  log.error("Unexpected error: {}", e.getMessage());
                  throw new RuntimeException("An unexpected error occurred: " + e.getMessage());
            }
      }

      public SessionResponse createSubscriptionSession(SubscriptionSessionRequest request) {
            SessionResponse sessionResponse = new SessionResponse();
            try {
                  Customer customer = findOrCreateCustomer(request.getEmail(), request.getUsername());
                  String clientUrl = "https://localhost:4200";

                  SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                          .setCustomer(customer.getId())
                          .setSuccessUrl(clientUrl + "/success-subscription?session_id={CHECKOUT_SESSION_ID}")
                          .setCancelUrl(clientUrl + "/failure")
                          .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                                  .setTrialPeriodDays(30L)
                                  .build());

                  String aPackage = String.valueOf(request.getData().get("PACKAGE"));
                  sessionCreateParamsBuilder.addLineItem(
                          SessionCreateParams.LineItem.builder()
                                  .setQuantity(1L)
                                  .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                          .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                  .putMetadata("package", aPackage)
                                                  .putMetadata("user_id", request.getUserId())
                                                  .setName(aPackage)
                                                  .build())
                                          .setCurrency("USD")
                                          .setUnitAmountDecimal(BigDecimal.valueOf(
                                                  Objects.equals(aPackage, "YEAR") ? 99.99 * 100 : 9.99 * 100))
                                          .setRecurring(SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                                  .setInterval(Objects.equals(aPackage, "YEAR") ?
                                                          SessionCreateParams.LineItem.PriceData.Recurring.Interval.YEAR :
                                                          SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                                  .build())
                                          .build())
                                  .build()
                  );

                  com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(sessionCreateParamsBuilder.build());

                  sessionResponse.setSessionUrl(stripeSession.getUrl());
                  sessionResponse.setSessionId(stripeSession.getId());

                  kafkaTemplate.send("payment_successful", NotificationEvent.builder()
                          .channel("email")
                          .recipient(request.getEmail())
                          .subject("Subscription Session Created")
                          .body("Your subscription session has been created successfully.")
                          .build());
            } catch (StripeException e) {
                  log.error("StripeService (createSubscriptionSession)", e);
                  sessionResponse.setMessage(e.getMessage());
            }
            return sessionResponse;
      }

      public StripeSubscriptionResponse cancelSubscription(String subscriptionId) {
            StripeSubscriptionResponse response = new StripeSubscriptionResponse();
            try {
                  Subscription subscription = Subscription.retrieve(subscriptionId);
                  Subscription canceledSubscription = subscription.cancel();

                  response.setSubscriptionId(canceledSubscription.getId());
                  response.setStatus(canceledSubscription.getStatus());
                  response.setMessage("Subscription canceled successfully.");
            } catch (StripeException e) {
                  log.error("StripeService (cancelSubscription)", e);
                  response.setMessage(e.getMessage());
            }
            return response;
      }

      private Customer findOrCreateCustomer(String email, String fullName) throws StripeException {
            CustomerSearchParams params =
                    CustomerSearchParams.builder()
                            .setQuery("email:'" + email + "'")
                            .build();

            CustomerSearchResult search = Customer.search(params);
            Customer customer;
            if (search.getData().isEmpty()) {
                  CustomerCreateParams customerCreateParams =
                          CustomerCreateParams.builder()
                                  .setName(fullName)
                                  .setEmail(email)
                                  .build();
                  customer = Customer.create(customerCreateParams);
            } else {
                  customer = search.getData().getFirst();
            }
            return customer;
      }

}