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

      @Override
      @Transactional
      public UserResponse register(RegistrationRequest request) {
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
                                  .build());
            } catch (FeignException e) {
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            String userId = extractUserId(creationResponse);

            User user = userMapper.toUser(request);
            user.setUserId(userId);
            user = userRepository.save(user);

            CompletableFuture.runAsync(() -> {
                  try {
                        identityClient.sendVerificationEmail(token, userId);
                  } catch (FeignException e) {
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
                        throw errorNormalizer.handleKeyCloakException(e);
                  }
            });
      }

      @Override
      @Transactional
      @PreAuthorize("hasRole('ADMIN')")
      public UserResponse updateUser(String userId, UserUpdateRequest updateRequest) {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

            try {
                  identityClient.updateUser("Bearer " + getAccessToken(), userId, updateRequest);
            } catch (FeignException e) {
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userMapper.updateUser(user, updateRequest);
            return userMapper.toUserResponse(userRepository.save(user));
      }

      @Override
      @Transactional
      public UserResponse updateMyInfo(UserUpdateRequest updateRequest) {
            String currentUserId = getCurrentUserId();

            try {
                  identityClient.updateUser("Bearer " + getAccessToken(), currentUserId, updateRequest);
            } catch (FeignException e) {
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            User user = userRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

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
                  throw errorNormalizer.handleKeyCloakException(e);
            }
      }

      @Override
      @Transactional
      @PreAuthorize("hasRole('ADMIN')")
      public void deleteUser(String userId) {
            if (!userRepository.existsByUserId(userId)) {
                  throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }

            try {
                  identityClient.deleteUser("Bearer " + getAccessToken(), userId);
            } catch (FeignException e) {
                  throw errorNormalizer.handleKeyCloakException(e);
            }

            userRepository.deleteByUserId(userId);
      }

      @Override
      public UserResponse getMyInfo() {
            String userId = getCurrentUserId();
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

            return userMapper.toUserResponse(user);
      }

      @Override
      public Page<UserResponse> getUsers(int page, int size) {
            Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
            return userPage.map(userMapper::toUserResponse);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public List<UserResponse> getAllUsers() {
            return userRepository.findAll().stream()
                    .map(userMapper::toUserResponse)
                    .toList();
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public UserResponse getUserByUserId(String userId) {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

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
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
            return authentication.getName();
      }

      private String extractUserId(ResponseEntity<?> response) {
            return Optional.ofNullable(response.getHeaders().get("Location"))
                    .map(headers -> headers.getFirst().split("/"))
                    .map(splitStr -> splitStr[splitStr.length - 1])
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
      }

      private String getAccessToken() {
            return identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build()).getAccessToken();
      }

}