package com.tien.payment.service.impl;

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
import com.tien.payment.exception.AppException;
import com.tien.payment.exception.ErrorCode;
import com.tien.payment.mapper.StripeMapper;
import com.tien.payment.repository.SessionRepository;
import com.tien.payment.repository.StripeChargeRepository;
import com.tien.payment.repository.StripeSubscriptionRepository;
import com.tien.payment.service.StripeService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeServiceImpl implements StripeService {

      StripeChargeRepository stripeChargeRepository;
      StripeSubscriptionRepository stripeSubscriptionRepository;
      SessionRepository sessionRepository;
      StripeMapper stripeMapper;
      KafkaTemplate<String, Object> kafkaTemplate;

      @Value("${stripe.api.key}")
      @NonFinal
      String stripeApiKey;

      @Value("${app.vip.price-ids.monthly}")
      @NonFinal
      String monthlyPriceId;

      @Value("${app.vip.price-ids.semiannual}")
      @NonFinal
      String semiannualPriceId;

      @Value("${app.vip.price-ids.annual}")
      @NonFinal
      String annualPriceId;

      @PostConstruct
      public void init() {
            Stripe.apiKey = stripeApiKey;
      }

      @Override
      public StripeChargeResponse charge(StripeChargeRequest request) {
            StripeCharge stripeCharge = stripeMapper.toStripeCharge(request);
            String currentUsername = getCurrentUsername();

            try {
                  Map<String, Object> chargeParams = new HashMap<>();
                  chargeParams.put("amount", (int) (request.getAmount() * 100));
                  chargeParams.put("currency", "USD");
                  chargeParams.put("description", "Payment for order by " + currentUsername);
                  chargeParams.put("source", request.getStripeToken());

                  HashMap<String, Object> metadata = new HashMap<>();
                  metadata.put("username", currentUsername);
                  chargeParams.put("metadata", metadata);

                  Charge charge = Charge.create(chargeParams);

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

                  stripeCharge.setUsername(currentUsername);
                  stripeChargeRepository.save(stripeCharge);
                  return stripeMapper.toStripeChargeResponse(stripeCharge);
            } catch (StripeException e) {
                  log.error("Payment failed for user {}: {}", currentUsername, e.getMessage());
                  throw new AppException(ErrorCode.PAYMENT_FAILED);
            }
      }

      @Override
      public StripeSubscriptionResponse createSubscription(StripeSubscriptionRequest request) {
            StripeSubscription stripeSubscription = stripeMapper.toStripeSubscription(request);
            String currentUsername = getCurrentUsername();

            try {
                  Map<String, Object> paymentMethodParams = new HashMap<>();
                  paymentMethodParams.put("type", "card");
                  paymentMethodParams.put("card", Map.of("token", request.getStripeToken()));
                  PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

                  Map<String, Object> customerMap = new HashMap<>();
                  customerMap.put("name", currentUsername);
                  customerMap.put("email", request.getEmail());
                  customerMap.put("payment_method", paymentMethod.getId());
                  Customer customer = Customer.create(customerMap);

                  String priceId = switch (request.getPackageType()) {
                        case "MONTHLY" -> monthlyPriceId;
                        case "SEMIANNUAL" -> semiannualPriceId;
                        case "ANNUAL" -> annualPriceId;
                        default -> throw new IllegalArgumentException("Invalid package type: " + request.getPackageType());
                  };

                  List<Object> items = new ArrayList<>();
                  items.add(Map.of("price", priceId, "quantity", request.getNumberOfLicense()));
                  Subscription subscription = Subscription.create(Map.of(
                          "customer", customer.getId(),
                          "default_payment_method", paymentMethod.getId(),
                          "items", items
                  ));

                  stripeSubscription.setStripeCustomerId(customer.getId());
                  stripeSubscription.setStripeSubscriptionId(subscription.getId());
                  stripeSubscription.setStripePaymentMethodId(paymentMethod.getId());
                  stripeSubscription.setUsername(currentUsername);
                  stripeSubscription.setPriceId(priceId);
                  stripeSubscription.setNumberOfLicense(request.getNumberOfLicense());
                  stripeSubscriptionRepository.save(stripeSubscription);

                  return StripeSubscriptionResponse.builder()
                          .id(subscription.getId())
                          .username(currentUsername)
                          .stripeCustomerId(customer.getId())
                          .stripeSubscriptionId(subscription.getId())
                          .stripePaymentMethodId(paymentMethod.getId())
                          .build();

            } catch (StripeException e) {
                  log.error("Subscription creation failed for user {}: {}", currentUsername, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_CREATION_FAILED);
            }
      }

      // The createPaymentSession method is currently under development and is not yet in use.
      @Override
      public SessionResponse createPaymentSession(PaymentSessionRequest request) {
            com.tien.payment.entity.Session paymentSession = stripeMapper.toSession(request);
            try {
                  BigDecimal amount = request.getAmount();
                  String productName = request.getProductName();
                  String currency = "USD";

                  String username = getCurrentUsername();
                  Customer customer = findOrCreateCustomer(request.getEmail(), username);
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
                                                  .putMetadata("username", username)
                                                  .setName(productName)
                                                  .build())
                                          .setCurrency(currency)
                                          .setUnitAmountDecimal(amount.multiply(BigDecimal.valueOf(100)))
                                          .build())
                                  .build());

                  SessionCreateParams.PaymentIntentData paymentIntentData =
                          SessionCreateParams.PaymentIntentData.builder()
                                  .putMetadata("username", username)
                                  .build();
                  sessionCreateParamsBuilder.setPaymentIntentData(paymentIntentData);

                  com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(sessionCreateParamsBuilder.build());

                  paymentSession.setSessionUrl(stripeSession.getUrl());
                  paymentSession.setSessionId(stripeSession.getId());
                  paymentSession.setUsername(username);
                  sessionRepository.save(paymentSession);

                  return stripeMapper.toSessionResponse(paymentSession);
            } catch (StripeException e) {
                  log.error("Payment session creation failed for user {}: {}", request.getUsername(), e.getMessage());
                  throw new AppException(ErrorCode.PAYMENT_SESSION_CREATION_FAILED);
            } catch (Exception e) {
                  log.error("An unexpected error occurred while creating payment session for user {}: {}", request.getUsername(), e.getMessage());
                  throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
      }

      // The createSubscriptionSession method is currently under development and is not yet in use.
      @Override
      public SessionResponse createSubscriptionSession(SubscriptionSessionRequest request) {
            SessionResponse sessionResponse = new SessionResponse();
            try {
                  String username = getCurrentUsername();
                  Customer customer = findOrCreateCustomer(request.getEmail(), username);
                  String clientUrl = "https://localhost:4200";

                  String priceId = switch (request.getPackageType()) {
                        case "MONTHLY" -> monthlyPriceId;
                        case "SEMIANNUAL" -> semiannualPriceId;
                        case "ANNUAL" -> annualPriceId;
                        default -> throw new IllegalArgumentException("Invalid package type: " + request.getPackageType());
                  };

                  SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                          .setCustomer(customer.getId())
                          .setSuccessUrl(clientUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                          .setCancelUrl(clientUrl + "/failure")
                          .addLineItem(SessionCreateParams.LineItem.builder()
                                  .setQuantity(1L)
                                  .setPrice(priceId)
                                  .build())
                          .build();

                  com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(sessionCreateParams);
                  sessionResponse.setSessionId(stripeSession.getId());
                  sessionResponse.setSessionUrl(stripeSession.getUrl());
                  sessionResponse.setUsername(username);

                  return sessionResponse;
            } catch (StripeException e) {
                  log.error("Subscription session creation failed for user {}: {}", request.getUsername(), e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_SESSION_CREATION_FAILED);
            } catch (Exception e) {
                  log.error("An unexpected error occurred while creating subscription session for user {}: {}", request.getUsername(), e.getMessage());
                  throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
      }

      @Override
      public void cancelSubscription(String subscriptionId) {
            String currentUsername = getCurrentUsername();

            StripeSubscription stripeSubscription = stripeSubscriptionRepository.findByStripeSubscriptionId(subscriptionId);
            if (stripeSubscription == null || !stripeSubscription.getUsername().equals(currentUsername)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            StripeSubscriptionResponse response = new StripeSubscriptionResponse();
            try {
                  Subscription subscription = Subscription.retrieve(subscriptionId);
                  Subscription canceledSubscription = subscription.cancel();

                  response.setStripeSubscriptionId(canceledSubscription.getId());
            } catch (StripeException e) {
                  log.error("Failed to cancel subscription with ID {}: {}", subscriptionId, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_CANCEL_FAILED);
            }
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<StripeSubscriptionResponse> retrieveAllSubscriptions() {
            List<StripeSubscription> subscriptions = stripeSubscriptionRepository.findAll();
            return subscriptions.stream()
                    .map(stripeMapper::toStripeSubscriptionResponse)
                    .collect(Collectors.toList());
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

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

}