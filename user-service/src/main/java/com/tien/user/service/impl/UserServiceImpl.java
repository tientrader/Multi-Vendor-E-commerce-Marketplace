package com.tien.user.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.user.dto.identity.Credential;
import com.tien.user.dto.identity.TokenExchangeParam;
import com.tien.user.dto.identity.TokenExchangeResponse;
import com.tien.user.dto.identity.UserCreationParam;
import com.tien.user.dto.request.*;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.entity.User;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.exception.ErrorNormalizer;
import com.tien.user.httpclient.IdentityClient;
import com.tien.user.kafka.KafkaProducer;
import com.tien.user.mapper.UserMapper;
import com.tien.user.repository.UserRepository;
import com.tien.user.service.UserService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

      UserRepository userRepository;
      UserMapper userMapper;
      IdentityClient identityClient;
      ErrorNormalizer errorNormalizer;
      KafkaProducer kafkaProducer;

      @Value("${idp.client-id}")
      @NonFinal
      String clientId;

      @Value("${idp.client-secret}")
      @NonFinal
      String clientSecret;

      @Value("${idp.redirect-uri}")
      @NonFinal
      String redirectUri;

      @Override
      @Transactional
      public UserResponse register(RegistrationRequest request) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                  throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
            }

            String token = getAccessToken();
            ResponseEntity<?> creationResponse;
            try {
                  creationResponse = identityClient.createUser(
                          "Bearer " + token,
                          UserCreationParam.builder()
                                  .username(request.getUsername())
                                  .firstName(request.getFirstName())
                                  .lastName(request.getLastName())
                                  .email(request.getEmail())
                                  .enabled(true)
                                  .emailVerified(false)
                                  .credentials(List.of(Credential.builder()
                                          .type("password")
                                          .temporary(false)
                                          .value(request.getPassword())
                                          .build()))
                                  .attributes(Map.of(
                                          "phoneNumber", List.of(request.getPhoneNumber()),
                                          "birthdate", List.of(request.getDob().toString())))
                                  .build());
            } catch (FeignException e) {
                  log.error("Error occurred during user registration: {}", e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            String userId = extractUserId(creationResponse);
            User user = userMapper.toUser(request);
            user.setUserId(userId);
            user = userRepository.save(user);

            CompletableFuture.runAsync(() -> {
                  try {
                        identityClient.sendVerificationEmail("Bearer " + token, userId);
                  } catch (FeignException e) {
                        log.error("Error occurred while sending verification email: {}", e.getMessage());
                        throw errorNormalizer.handleKeyCloakException(e);
                  }
            });

            kafkaProducer.send("register-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Welcome to TienProApp")
                    .body("Hello, " + request.getUsername())
                    .build());

            return userMapper.toUserResponse(user);
      }

      @Override
      @Transactional
      public TokenResponse login(String code) {
            TokenExchangeParam tokenExchangeParam = TokenExchangeParam.builder()
                    .grant_type("authorization_code")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .code(code)
                    .redirect_uri(redirectUri)
                    .build();

            TokenExchangeResponse tokenResponse;
            try {
                  tokenResponse = identityClient.exchangeToken(tokenExchangeParam);
            } catch (FeignException e) {
                  log.error("Error during token exchange with Keycloak: {}", e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            UserInfo userInfo = identityClient.getUserInfo("Bearer " + tokenResponse.getAccessToken());

            userRepository.findByEmail(userInfo.getEmail())
                    .orElseGet(() -> {
                          User newUser = User.builder()
                                  .username(userInfo.getPreferredUsername())
                                  .firstName(userInfo.getGivenName())
                                  .lastName(userInfo.getFamilyName())
                                  .email(userInfo.getEmail())
                                  .userId(userInfo.getSub())
                                  .phoneNumber(userInfo.getPhoneNumber())
                                  .dob(LocalDate.parse(userInfo.getBirthdate()))
                                  .build();

                          userRepository.save(newUser);

                          kafkaProducer.send("register-successful", NotificationEvent.builder()
                                  .channel("EMAIL")
                                  .recipient(newUser.getEmail())
                                  .subject("Welcome to TienProApp")
                                  .body("Hello, " + newUser.getUsername())
                                  .build());

                          return newUser;
                    });

            return userMapper.toUserLoginResponse(tokenResponse);
      }

      @Override
      @Transactional
      public TokenResponse login(UserLoginRequest request) {
            TokenExchangeParam tokenExchangeParam = TokenExchangeParam.builder()
                    .grant_type("password")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .otp(request.getOtp())
                    .build();

            try {
                  TokenExchangeResponse tokenResponse = identityClient.exchangeToken(tokenExchangeParam);
                  return userMapper.toUserLoginResponse(tokenResponse);
            } catch (FeignException e) {
                  log.error("Error occurred during login for user {}: {}", request.getUsername(), e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }
      }

      @Override
      @Transactional
      public TokenResponse refreshToken(RefreshTokenRequest request) {
            TokenExchangeParam tokenExchangeParam = TokenExchangeParam.builder()
                    .grant_type("refresh_token")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .refresh_token(request.getRefreshToken())
                    .build();

            try {
                  TokenExchangeResponse tokenResponse = identityClient.exchangeToken(tokenExchangeParam);
                  return userMapper.toUserLoginResponse(tokenResponse);
            } catch (FeignException e) {
                  log.error("Error occurred during token refresh: {}", e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }
      }

      @Override
      @Transactional
      public void logout() {
            String userId = getCurrentUserId();

            try {
                  identityClient.logoutUser("Bearer " + getAccessToken(), userId);
            } catch (FeignException e) {
                  log.error("Error occurred during logout for user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }
      }

      @Override
      @Transactional
      public void forgotPassword(ForgotPasswordRequest request) {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
            String userId = user.getUserId();

            CompletableFuture.runAsync(() -> {
                  try {
                        identityClient.sendResetPasswordEmail("Bearer " + getAccessToken(), userId);
                  } catch (FeignException e) {
                        log.error("Error occurred while sending reset password email for user {}: {}", request.getEmail(), e.getMessage());
                        throw errorNormalizer.handleKeyCloakException(e);
                  }
            });
      }

      @Override
      @Transactional
      @PreAuthorize("hasRole('ADMIN')")
      public UserResponse updateUser(String userId, UserUpdateRequest updateRequest) {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            try {
                  identityClient.updateUser("Bearer " + getAccessToken(), userId, updateRequest);
            } catch (FeignException e) {
                  log.error("Error occurred while updating user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userMapper.updateUser(user, updateRequest);
            return userMapper.toUserResponse(userRepository.save(user));
      }

      @Override
      @Transactional
      public UserResponse updateMyInfo(UserUpdateRequest updateRequest) {
            String userId = getCurrentUserId();
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            try {
                  identityClient.updateUser("Bearer " + getAccessToken(), userId, updateRequest);
            } catch (FeignException e) {
                  log.error("Error occurred while updating current user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userMapper.updateUser(user, updateRequest);
            return userMapper.toUserResponse(userRepository.save(user));
      }

      @Override
      @Transactional
      public void resetPassword(ResetPasswordRequest request) {
            String userId = getCurrentUserId();

            try {
                  identityClient.resetPassword("Bearer " + getAccessToken(), userId,
                          Credential.builder()
                                  .type("password")
                                  .temporary(false)
                                  .value(request.getNewPassword())
                                  .build());
            } catch (FeignException e) {
                  log.error("Error occurred during password reset for user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }
      }

      @Override
      @Transactional
      @PreAuthorize("hasRole('ADMIN')")
      public void deleteUser(String userId) {
            userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            try {
                  identityClient.deleteUser("Bearer " + getAccessToken(), userId);
            } catch (FeignException e) {
                  log.error("Error occurred while deleting user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userRepository.deleteByUserId(userId);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<UserResponse> getAllUsers() {
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::toUserResponse)
                    .toList();
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public UserResponse getUserByUserId(String userId) {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return userMapper.toUserResponse(user);
      }

      @Override
      public Page<UserResponse> searchUsers(String keyword, int page, int size) {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<User> users = userRepository.searchUsers(keyword, pageRequest);
            return users.map(userMapper::toUserResponse);
      }

      @Override
      public Page<UserResponse> getUsers(int page, int size) {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
            return userPage.map(userMapper::toUserResponse);
      }

      @Override
      public UserResponse getMyInfo() {
            String userId = getCurrentUserId();
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return userMapper.toUserResponse(user);
      }

      @Override
      public UserResponse getUserByUsername(String username) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return userMapper.toUserResponse(user);
      }

      @Override
      public UserResponse getUserByProfileId(String profileId) {
            User user = userRepository.findById(profileId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
            return userMapper.toUserResponse(user);
      }

      private String getCurrentUserId() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                  log.error("Failed to retrieve current user ID: Authentication is null or not authenticated");
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
            return authentication.getName();
      }

      private String extractUserId(ResponseEntity<?> response) {
            return Optional.ofNullable(response.getHeaders().get("Location"))
                    .map(headers -> headers.getFirst().split("/"))
                    .map(splitStr -> splitStr[splitStr.length - 1])
                    .orElseThrow(() -> {
                          log.error("Failed to extract user ID from response headers");
                          return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });
      }

      private String getAccessToken() {
            return identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build())
                    .getAccessToken();
      }

}