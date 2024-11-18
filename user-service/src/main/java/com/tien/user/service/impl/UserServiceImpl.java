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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
      KafkaTemplate<String, Object> kafkaTemplate;

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
            String token = getAccessToken();
            ResponseEntity<?> creationResponse;

            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                  throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
            }

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
                                  .build());
            } catch (FeignException e) {
                  log.error("Error occurred during user registration: {}", e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            String userId = extractUserId(creationResponse);

            User user = userMapper.toUser(request);
            user.setUserId(userId);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);

            CompletableFuture.runAsync(() -> {
                  try {
                        identityClient.sendVerificationEmail("Bearer " + token, userId);
                  } catch (FeignException e) {
                        log.error("Error occurred while sending verification email: {}", e.getMessage());
                        throw errorNormalizer.handleKeyCloakException(e);
                  }
            });

            kafkaTemplate.send("register-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Welcome to TienProApp")
                    .body("Hello, " + request.getUsername())
                    .build());

            return userMapper.toUserResponse(user);
      }

      @Override
      @Transactional
      public TokenResponse socialLogin(String code) {
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
                    .orElseGet(() -> userRepository.save(User.builder()
                            .username(userInfo.getEmail())
                            .firstName(userInfo.getGivenName())
                            .lastName(userInfo.getFamilyName())
                            .email(userInfo.getEmail())
                            .userId(userInfo.getSub())
                            .build()));

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
            User user = findUserByEmail(request.getEmail());
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
            User user = findUserById(userId);
            user.setUpdatedAt(LocalDateTime.now());

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
            User user = findUserById(userId);
            user.setUpdatedAt(LocalDateTime.now());

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
            findUserById(userId);

            try {
                  identityClient.deleteUser("Bearer " + getAccessToken(), userId);
            } catch (FeignException e) {
                  log.error("Error occurred while deleting user {}: {}", userId, e.getMessage());
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userRepository.deleteByUserId(userId);
      }

      @Override
      public Page<UserResponse> getUsers(int page, int size) {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
            return userPage.map(userMapper::toUserResponse);
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
      public UserResponse getMyInfo() {
            return userMapper.toUserResponse(findUserById(getCurrentUserId()));
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public UserResponse getUserByUserId(String userId) {
            return userMapper.toUserResponse(findUserById(userId));
      }

      @Override
      public UserResponse getUserByProfileId(String profileId) {
            return userMapper.toUserResponse(findUserByProfileId(profileId));
      }

      @Override
      public UserResponse getUserByUsername(String username) {
            return userMapper.toUserResponse(findUserByUsername(username));
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
                    .build()).getAccessToken();
      }

      private User findUserById(String userId) {
            return userRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                          log.error("User with ID {} not found", userId);
                          return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });
      }

      private User findUserByProfileId(String profileId) {
            return userRepository.findById(profileId)
                    .orElseThrow(() -> {
                          log.error("User with profileID {} not found", profileId);
                          return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                    });
      }

      private User findUserByEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                          log.error("User with email {} not found", email);
                          return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                    });
      }

      private User findUserByUsername(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                          log.error("User with username {} not found", username);
                          return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                    });
      }

}