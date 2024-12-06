package com.tien.payment.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.tien.event.dto.NotificationEvent;
import com.tien.event.dto.PaymentResponse;
import com.tien.payment.dto.request.PaymentSessionRequest;
import com.tien.event.dto.StripeChargeRequest;
import com.tien.payment.dto.request.StripeSubscriptionRequest;
import com.tien.payment.dto.request.SubscriptionSessionRequest;
import com.tien.payment.dto.response.SessionResponse;
import com.tien.payment.dto.response.StripeChargeResponse;
import com.tien.payment.dto.response.StripeSubscriptionResponse;
import com.tien.payment.entity.StripeCharge;
import com.tien.payment.entity.StripeSubscription;
import com.tien.payment.exception.AppException;
import com.tien.payment.exception.ErrorCode;
import com.tien.payment.kafka.KafkaProducer;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeServiceImpl implements StripeService {

      StripeChargeRepository stripeChargeRepository;
      StripeSubscriptionRepository stripeSubscriptionRepository;
      SessionRepository sessionRepository;
      StripeMapper stripeMapper;
      KafkaProducer kafkaProducer;

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
            String currentUsername = getCurrentUsername();
            request.setUsername(currentUsername);

            return processCharge(request);
      }

      @Override
      public StripeChargeResponse processCharge(StripeChargeRequest request) {
            StripeCharge stripeCharge = stripeMapper.toStripeCharge(request);
            String username = request.getUsername();

            String stripeToken = request.getStripeToken() != null ? request.getStripeToken() : "tok_visa";

            try {
                  Map<String, Object> chargeParams = new HashMap<>();
                  chargeParams.put("amount", (int) (request.getAmount() * 100));
                  chargeParams.put("currency", "USD");
                  chargeParams.put("description", "Payment for order by " + username);
                  chargeParams.put("source", stripeToken);

                  HashMap<String, Object> metadata = new HashMap<>();
                  metadata.put("username", username);
                  chargeParams.put("metadata", metadata);

                  Charge charge = Charge.create(chargeParams);

                  if (charge.getPaid()) {
                        stripeCharge.setChargeId(charge.getId());
                        stripeCharge.setSuccess(true);

                        kafkaProducer.send("payment-response", PaymentResponse.builder()
                                .orderId(request.getOrderId())
                                .success(true)
                                .build());

                        kafkaProducer.send("payment_successful", NotificationEvent.builder()
                                .channel("email")
                                .recipient(request.getEmail())
                                .subject("Payment Successful")
                                .body("Your payment of " + request.getAmount() + " USD was successful.")
                                .build());
                  } else {
                        stripeCharge.setSuccess(false);

                        kafkaProducer.send("payment-response", PaymentResponse.builder()
                                .orderId(request.getOrderId())
                                .success(false)
                                .build());
                  }

                  stripeCharge.setUsername(username);
                  stripeChargeRepository.save(stripeCharge);
                  return stripeMapper.toStripeChargeResponse(stripeCharge);
            } catch (StripeException e) {
                  log.error("Payment failed for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.PAYMENT_FAILED);
            }
      }

      @Override
      public StripeSubscriptionResponse createSubscription(StripeSubscriptionRequest request) {
            StripeSubscription stripeSubscription = stripeMapper.toStripeSubscription(request);
            String currentUsername = getCurrentUsername();

            String stripeToken = request.getStripeToken() != null ? request.getStripeToken() : "tok_visa";
            long numberOfLicense = request.getNumberOfLicense() > 0 ? request.getNumberOfLicense() : 1;

            try {
                  Map<String, Object> paymentMethodParams = new HashMap<>();
                  paymentMethodParams.put("type", "card");
                  paymentMethodParams.put("card", Map.of("token", stripeToken));
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
                  items.add(Map.of("price", priceId, "quantity", numberOfLicense));

                  Map<String, Object> subscriptionParams = new HashMap<>();
                  subscriptionParams.put("customer", customer.getId());
                  subscriptionParams.put("default_payment_method", paymentMethod.getId());
                  subscriptionParams.put("items", items);
                  subscriptionParams.put("metadata", Map.of(
                          "username", currentUsername,
                          "packageType", request.getPackageType()
                  ));

                  Subscription subscription = Subscription.create(subscriptionParams);

                  stripeSubscription.setStripeCustomerId(customer.getId());
                  stripeSubscription.setStripeSubscriptionId(subscription.getId());
                  stripeSubscription.setStripePaymentMethodId(paymentMethod.getId());
                  stripeSubscription.setUsername(currentUsername);
                  stripeSubscription.setPriceId(priceId);
                  stripeSubscription.setNumberOfLicense(numberOfLicense);
                  stripeSubscriptionRepository.save(stripeSubscription);

                  return StripeSubscriptionResponse.builder()
                          .username(currentUsername)
                          .stripeCustomerId(customer.getId())
                          .build();

            } catch (StripeException e) {
                  log.error("Subscription creation failed for user {}: {}", currentUsername, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_CREATION_FAILED);
            }
      }

      @Override
      public SessionResponse createPaymentSession(PaymentSessionRequest request) {
            com.tien.payment.entity.Session paymentSession = stripeMapper.toSession(request);
            String username = getCurrentUsername();

            try {
                  BigDecimal amount = request.getAmount();
                  String productName = request.getProductName();
                  String currency = "USD";

                  Customer customer = findOrCreateCustomer(request.getEmail(), username);
                  String clientUrl = "https://localhost:3000";

                  SessionCreateParams.Builder sessionCreateParamsBuilder = SessionCreateParams.builder()
                          .setMode(SessionCreateParams.Mode.PAYMENT)
                          .setCustomer(customer.getId())
                          .setSuccessUrl(clientUrl + "/success")
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
                  log.error("Payment session creation failed for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.PAYMENT_SESSION_CREATION_FAILED);
            } catch (Exception e) {
                  log.error("An unexpected error occurred while creating payment session for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
      }

      @Override
      public SessionResponse createSubscriptionSession(SubscriptionSessionRequest request) {
            String username = getCurrentUsername();
            SessionResponse sessionResponse = new SessionResponse();

            try {
                  Customer customer = findOrCreateCustomer(request.getEmail(), username);
                  String clientUrl = "https://localhost:3000";

                  String priceId = switch (request.getPackageType()) {
                        case "MONTHLY" -> monthlyPriceId;
                        case "SEMIANNUAL" -> semiannualPriceId;
                        case "ANNUAL" -> annualPriceId;
                        default -> throw new IllegalArgumentException("Invalid package type: " + request.getPackageType());
                  };

                  SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                          .setCustomer(customer.getId())
                          .setSuccessUrl(clientUrl + "/success")
                          .setCancelUrl(clientUrl + "/failure")
                          .putMetadata("username", username)
                          .putMetadata("packageType", request.getPackageType())
                          .addLineItem(SessionCreateParams.LineItem.builder()
                                  .setQuantity(1L)
                                  .setPrice(priceId)
                                  .build())
                          .build();

                  com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(sessionCreateParams);

                  com.tien.payment.entity.Session session = com.tien.payment.entity.Session.builder()
                          .sessionId(stripeSession.getId())
                          .sessionUrl(stripeSession.getUrl())
                          .username(username)
                          .build();
                  sessionRepository.save(session);

                  sessionResponse.setSessionId(stripeSession.getId());
                  sessionResponse.setSessionUrl(stripeSession.getUrl());
                  sessionResponse.setUsername(username);

                  return sessionResponse;
            } catch (StripeException e) {
                  log.error("Subscription session creation failed for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_SESSION_CREATION_FAILED);
            } catch (Exception e) {
                  log.error("An unexpected error occurred while creating subscription session for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
      }

      @Override
      public void cancelSubscription(String subscriptionId) {
            try {
                  Subscription subscription = Subscription.retrieve(subscriptionId);
                  subscription.cancel();
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
                    .toList();
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