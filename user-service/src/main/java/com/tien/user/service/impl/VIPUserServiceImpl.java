package com.tien.user.service.impl;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.StripeSubscriptionRequest;
import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.StripeSubscriptionResponse;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.entity.User;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.httpclient.PaymentClient;
import com.tien.user.repository.UserRepository;
import com.tien.user.service.VIPUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VIPUserServiceImpl implements VIPUserService {

      UserRepository userRepository;
      PaymentClient paymentClient;

      @Override
      public VIPUserResponse createVIPUser(VIPUserRequest request) {
            try {
                  String username = getCurrentUsername();

                  StripeSubscriptionRequest subscriptionRequest = StripeSubscriptionRequest.builder()
                          .stripeToken(request.getStripeToken())
                          .email(request.getEmail())
                          .priceId(request.getPriceId())
                          .username(username)
                          .numberOfLicense(request.getNumberOfLicense())
                          .build();

                  ApiResponse<StripeSubscriptionResponse> subscriptionResponse = paymentClient.createSubscription(subscriptionRequest);

                  User user = userRepository.findByUsername(username)
                          .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                  LocalDate vipStartDate = LocalDate.now();
                  user.setVipStartDate(vipStartDate);

                  LocalDate vipEndDate = switch (request.getPriceId()) {
                        case "price_1QAX8ZC9fPubZMQMNeWli1FU" ->
                                vipStartDate.plusMonths(1);
                        case "price_1QAX8ZC9fPubZMQMmdG6vK1G" ->
                                vipStartDate.plusMonths(6);
                        case "price_1QBe0vC9fPubZMQMR1GiXMX4" ->
                                vipStartDate.plusYears(1);
                        default ->
                                throw new RuntimeException("Invalid priceId");
                  };

                  user.setVipEndDate(vipEndDate);
                  user.setStripeSubscriptionId(subscriptionResponse.getResult().getStripeSubscriptionId());
                  user.setVipStatus(true);
                  userRepository.save(user);

                  return VIPUserResponse.builder()
                          .stripeSubscriptionId(user.getStripeSubscriptionId())
                          .username(user.getUsername())
                          .email(request.getEmail())
                          .vipStatus(true)
                          .vipStartDate(vipStartDate)
                          .vipEndDate(vipEndDate)
                          .build();
            } catch (Exception e) {
                  log.error("Error creating VIP user: {}", e.getMessage());
                  return VIPUserResponse.builder()
                          .stripeSubscriptionId(null)
                          .username(request.getUsername())
                          .email(request.getEmail())
                          .vipStatus(false)
                          .vipStartDate(null)
                          .vipEndDate(null)
                          .build();
            }
      }

      @Override
      public VIPUserResponse cancelVIPUserSubscription(String username) {
            String currentUsername = getCurrentUsername();

            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getStripeSubscriptionId() != null) {
                  paymentClient.cancelSubscription(user.getStripeSubscriptionId());
                  user.setVipStatus(false);
                  user.setVipStartDate(null);
                  user.setVipEndDate(null);
                  user.setStripeSubscriptionId(null);
                  userRepository.save(user);
            }

            return VIPUserResponse.builder()
                    .stripeSubscriptionId(null)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .vipStatus(false)
                    .build();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

}