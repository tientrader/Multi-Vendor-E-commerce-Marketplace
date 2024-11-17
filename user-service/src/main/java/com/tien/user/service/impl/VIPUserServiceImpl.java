package com.tien.user.service.impl;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.StripeSubscriptionRequest;
import com.tien.user.dto.request.SubscriptionSessionRequest;
import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.dto.response.SessionResponse;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;
import com.tien.user.entity.User;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.httpclient.PaymentClient;
import com.tien.user.mapper.VIPUserMapper;
import com.tien.user.repository.UserRepository;
import com.tien.user.service.VIPUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VIPUserServiceImpl implements VIPUserService {

      UserRepository userRepository;
      PaymentClient paymentClient;
      VIPUserMapper vipUserMapper;

      @Override
      @Transactional
      public void createVIPUser(VIPUserRequest request) {
            String username = getCurrentUsername();

            String stripeToken = request.getStripeToken() != null ? request.getStripeToken() : "tok_visa";
            long numberOfLicense = request.getNumberOfLicense() > 0 ? request.getNumberOfLicense() : 1;

            StripeSubscriptionRequest subscriptionRequest = StripeSubscriptionRequest.builder()
                    .stripeToken(stripeToken)
                    .email(request.getEmail())
                    .packageType(request.getPackageType())
                    .username(username)
                    .numberOfLicense(numberOfLicense)
                    .build();

            try {
                  paymentClient.createSubscription(subscriptionRequest);
            } catch (Exception e) {
                  log.error("Failed to create subscription for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_CREATION_FAILED);
            }

            findOrCreateUser(request, username);
      }

      @Override
      @Transactional
      public VIPUserResponseWithSession createVIPUserWithSession(VIPUserRequestWithSession request) {
            String username = getCurrentUsername();

            SubscriptionSessionRequest sessionRequest = SubscriptionSessionRequest.builder()
                    .email(request.getEmail())
                    .username(username)
                    .packageType(request.getPackageType())
                    .build();

            ApiResponse<SessionResponse> sessionResponse;
            try {
                  sessionResponse = paymentClient.createSubscriptionSession(sessionRequest);
            } catch (Exception e) {
                  log.error("Failed to create subscription session for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.SUBSCRIPTION_SESSION_CREATION_FAILED);
            }

            User user = findOrCreateUser(request, username);
            String sessionUrl = sessionResponse.getResult().getSessionUrl();

            VIPUserResponseWithSession vipUserResponseWithSession = vipUserMapper.userToVipUserResponseWithSession(user);
            vipUserResponseWithSession.setSessionUrl(sessionUrl);

            return vipUserResponseWithSession;
      }

      @Override
      @Transactional
      public void updateVipEndDate(String username, String packageType, String subscriptionId) {
            User user = findUserByUsername(username);
            LocalDate vipStartDate = LocalDate.now();
            LocalDate vipEndDate = getVipEndDateBasedOnPackageType(packageType, vipStartDate);

            user.setVipStartDate(vipStartDate);
            user.setVipEndDate(vipEndDate);
            user.setVipStatus(true);
            user.setStripeSubscriptionId(subscriptionId);

            userRepository.save(user);
      }

      @Override
      @Transactional
      public void cancelVIPUserSubscription() {
            String currentUsername = getCurrentUsername();
            User user = findUserByUsername(currentUsername);

            if (user.getStripeSubscriptionId() != null) {
                  try {
                        paymentClient.cancelSubscription(user.getStripeSubscriptionId());
                  } catch (Exception e) {
                        log.error("Failed to cancel subscription for user {}: {}", currentUsername, e.getMessage());
                        throw new AppException(ErrorCode.SUBSCRIPTION_CANCELLATION_FAILED);
                  }
            }

            userRepository.save(user);
      }

      @Override
      public VIPUserResponse checkIfUserIsVIP(String username) {
            User user = findUserByUsername(username);

            if (!user.isVipStatus() || (user.getVipEndDate() != null && user.getVipEndDate().isBefore(LocalDate.now()))) {
                  log.error("User {} is not a VIP user or has canceled their subscription.", username);
                  throw new AppException(ErrorCode.USER_NOT_VIP);
            }

            return vipUserMapper.userToVipUserResponse(user);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private LocalDate getVipEndDateBasedOnPackageType(String packageType, LocalDate vipStartDate) {
            return switch (packageType) {
                  case "MONTHLY" -> vipStartDate.plusMonths(1);
                  case "SEMIANNUAL" -> vipStartDate.plusMonths(6);
                  case "ANNUAL" -> vipStartDate.plusYears(1);
                  default -> {
                        log.error("Invalid package type provided: {}", packageType);
                        throw new AppException(ErrorCode.INVALID_PACKAGE_TYPE);
                  }
            };
      }

      private User findUserByUsername(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                          log.error("User with username {} not found", username);
                          return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });
      }

      private void findOrCreateUser(VIPUserRequest request, String username) {
            userRepository.findByUsername(username);
            vipUserMapper.vipUserRequestToUser(request);
      }

      private User findOrCreateUser(VIPUserRequestWithSession request, String username) {
            return userRepository.findByUsername(username)
                    .orElse(vipUserMapper.vipUserRequestWithSessionToUser(request));
      }

}