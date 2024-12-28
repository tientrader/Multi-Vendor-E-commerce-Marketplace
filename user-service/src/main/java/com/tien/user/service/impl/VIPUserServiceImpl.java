package com.tien.user.service.impl;

import com.tien.user.dto.ApiResponse;
import com.tien.user.enums.PackageType;
import com.tien.user.httpclient.request.SubscriptionSessionRequest;
import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.httpclient.response.SessionResponse;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;
import com.tien.user.entity.User;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.httpclient.PaymentClient;
import com.tien.user.mapper.VIPUserMapper;
import com.tien.user.repository.UserRepository;
import com.tien.user.service.VIPUserService;
import feign.FeignException;
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
      public VIPUserResponseWithSession createVIPUserWithSession(VIPUserRequestWithSession request) {
            String username = getCurrentUsername();
            String email = getCurrentEmail();

            SubscriptionSessionRequest sessionRequest = SubscriptionSessionRequest.builder()
                    .email(email)
                    .username(username)
                    .packageType(request.getPackageType())
                    .build();

            ApiResponse<SessionResponse> sessionResponse;
            try {
                  sessionResponse = paymentClient.createSubscriptionSession(sessionRequest);
            } catch (FeignException e) {
                  log.error("Failed to create subscription session for user {}: {}", username, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            User user = userRepository.findByUsername(username)
                    .orElse(vipUserMapper.vipUserRequestWithSessionToUser(request));

            String id = sessionResponse.getResult().getId();
            String sessionUrl = sessionResponse.getResult().getSessionUrl();

            VIPUserResponseWithSession vipUserResponseWithSession = vipUserMapper.userToVipUserResponseWithSession(user);
            vipUserResponseWithSession.setId(id);
            vipUserResponseWithSession.setSessionUrl(sessionUrl);

            return vipUserResponseWithSession;
      }

      @Override
      @Transactional
      public void updateVipEndDate(String username, PackageType packageType, String subscriptionId) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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

            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            if (user.getStripeSubscriptionId() == null) {
                  log.warn("User {} does not have a subscription to cancel.", currentUsername);
                  throw new AppException(ErrorCode.NO_ACTIVE_SUBSCRIPTION);
            }

            try {
                  paymentClient.cancelSubscription(user.getStripeSubscriptionId());
            } catch (FeignException e) {
                  log.error("Failed to cancel subscription for user {}: {}", currentUsername, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            user.setStripeSubscriptionId(null);
            userRepository.save(user);
      }

      @Override
      public VIPUserResponse checkIfUserIsVIP(String username) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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

      private String getCurrentEmail() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("email");
      }

      private LocalDate getVipEndDateBasedOnPackageType(PackageType packageType, LocalDate vipStartDate) {
            return switch (packageType) {
                  case MONTHLY -> vipStartDate.plusMonths(1);
                  case SEMIANNUAL -> vipStartDate.plusMonths(6);
                  case ANNUAL -> vipStartDate.plusYears(1);
            };
      }

}