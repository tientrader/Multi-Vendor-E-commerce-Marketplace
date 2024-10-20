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
import com.tien.user.mapper.VIPUserMapper;
import com.tien.user.repository.UserRepository;
import com.tien.user.service.VIPUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
      VIPUserMapper vipUserMapper;

      @Value("${app.vip.price-ids.monthly}")
      @NonFinal
      String monthlyPriceId;

      @Value("${app.vip.price-ids.semiannual}")
      @NonFinal
      String semiannualPriceId;

      @Value("${app.vip.price-ids.annual}")
      @NonFinal
      String annualPriceId;

      @Override
      public VIPUserResponse createVIPUser(VIPUserRequest request) {
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
                    .orElse(vipUserMapper.vipUserRequestToUser(request));

            LocalDate vipStartDate = LocalDate.now();
            user.setVipStartDate(vipStartDate);

            LocalDate vipEndDate = getVipEndDateBasedOnPriceId(request.getPriceId(), vipStartDate);
            user.setVipEndDate(vipEndDate);
            user.setStripeSubscriptionId(subscriptionResponse.getResult().getStripeSubscriptionId());
            user.setVipStatus(true);
            userRepository.save(user);

            return vipUserMapper.userToVipUserResponse(user);
      }

      @Override
      public VIPUserResponse cancelVIPUserSubscription(String username) {
            String currentUsername = getCurrentUsername();

            User user = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            if (user.getStripeSubscriptionId() != null) {
                  paymentClient.cancelSubscription(user.getStripeSubscriptionId());
                  user.setVipStatus(false);
                  user.setVipStartDate(null);
                  user.setVipEndDate(null);
                  user.setStripeSubscriptionId(null);
                  userRepository.save(user);
            }

            return vipUserMapper.userToVipUserResponse(user);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private LocalDate getVipEndDateBasedOnPriceId(String priceId, LocalDate vipStartDate) {
            if (priceId.equals(monthlyPriceId)) {
                  return vipStartDate.plusMonths(1);
            } else if (priceId.equals(semiannualPriceId)) {
                  return vipStartDate.plusMonths(6);
            } else if (priceId.equals(annualPriceId)) {
                  return vipStartDate.plusYears(1);
            } else {
                  throw new AppException(ErrorCode.INVALID_PRICE_ID);
            }
      }

}