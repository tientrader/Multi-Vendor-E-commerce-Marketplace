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
        log.info("Starting user registration for username: {}", request.getUsername());
        try {
            String token = getAccessToken();
            log.debug("Token retrieved: {}", token);

            ResponseEntity<?> creationResponse = identityClient.createUser(
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

            String userId = extractUserId(creationResponse);
            log.info("User created with userId: {}", userId);

            User user = userMapper.toUser(request);
            user.setUserId(userId);
            user = userRepository.save(user);

            log.debug("Sending verification email for userId: {}", userId);
            CompletableFuture.runAsync(() -> {
                try {
                    identityClient.sendVerificationEmail(token, userId);
                    log.info("Verification email sent successfully for userId: {}", userId);
                } catch (FeignException e) {
                    log.error("FeignException during sending verification email for userId: {}", userId, e);
                    throw errorNormalizer.handleKeyCloakException(e);
                }
            });

            log.info("Sending Kafka message for user registration: {}", request.getEmail());
            kafkaTemplate.send("register-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Welcome to TienProApp")
                    .body("Hello, " + request.getUsername())
                    .build());

            log.info("User registered successfully with userId: {}", userId);
            return userMapper.toUserResponse(user);
        } catch (FeignException e) {
            log.error("FeignException during registration for username: {}", request.getUsername(), e);
            throw errorNormalizer.handleKeyCloakException(e);
        }
    }

    @Override
    @Transactional
    public TokenResponse login(UserLoginRequest request) {
        log.info("User {} is attempting to log in", request.getUsername());

        TokenExchangeParam tokenExchangeParam = TokenExchangeParam.builder()
                .grant_type("password")
                .client_id(clientId)
                .client_secret(clientSecret)
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        try {
            TokenExchangeResponse tokenResponse = identityClient.exchangeToken(tokenExchangeParam);
            log.info("User {} logged in successfully", request.getUsername());
            return userMapper.toUserLoginResponse(tokenResponse);
        } catch (FeignException e) {
            log.error("FeignException caught while refreshing login: Status {}, Message: {}", e.status(), e.getMessage());
            throw errorNormalizer.handleKeyCloakException(e);
        }
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        log.info("Refreshing token for user");

        TokenExchangeParam tokenExchangeParam = TokenExchangeParam.builder()
                .grant_type("refresh_token")
                .client_id(clientId)
                .client_secret(clientSecret)
                .refresh_token(refreshToken)
                .build();

        try {
            TokenExchangeResponse tokenResponse = identityClient.exchangeToken(tokenExchangeParam);
            log.info("Token refreshed successfully");
            return userMapper.toUserLoginResponse(tokenResponse);
        } catch (FeignException e) {
            log.error("FeignException caught while refreshing token: Status {}, Message: {}", e.status(), e.getMessage());
            throw errorNormalizer.handleKeyCloakException(e);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Sending reset password email for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("(forgotPassword) Profile not found for email: {}", request.getEmail());
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        String userId = user.getUserId();
        log.info("UserId retrieved: {}", userId);

        CompletableFuture.runAsync(() -> {
            try {
                identityClient.sendResetPasswordEmail("Bearer " + getAccessToken(), userId);
                log.info("Reset password email sent successfully for userId: {}", userId);
            } catch (FeignException e) {
                log.error("FeignException during sending reset password email for userId: {}", userId, e);
                throw errorNormalizer.handleKeyCloakException(e);
            }
        });
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(String userId, UserUpdateRequest updateRequest) {
        log.info("Updating user with userId: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("(updateUser) Profile not found for userId: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        if (isEmailExists(updateRequest.getEmail(), userId)) {
            log.error("(updateUser) Email already exists: {}", updateRequest.getEmail());
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        try {
            identityClient.updateUser("Bearer " + getAccessToken(), userId, updateRequest);
        } catch (FeignException e) {
            log.error("FeignException during user update for userId: {}", userId, e);
            throw errorNormalizer.handleKeyCloakException(e);
        }

        userMapper.updateUser(user, updateRequest);
        UserResponse updatedUser = userMapper.toUserResponse(userRepository.save(user));
        log.info("User updated successfully with userId: {}", userId);

        return updatedUser;
    }

    @Override
    @Transactional
    public UserResponse updateMyInfo(UserUpdateRequest updateRequest) {
        String currentUserId = getCurrentUserId();
        log.info("Updating own info for userId: {}", currentUserId);

        if (isEmailExists(updateRequest.getEmail(), currentUserId)) {
            log.error("(updateMyInfo) Email already exists: {}", updateRequest.getEmail());
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        try {
            identityClient.updateUser("Bearer " + getAccessToken(), currentUserId, updateRequest);
        } catch (FeignException e) {
            log.error("FeignException during user update for userId: {}", currentUserId, e);
            throw errorNormalizer.handleKeyCloakException(e);
        }

        User user = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> {
                    log.error("(updateMyInfo) Profile not found for userId: {}", currentUserId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        userMapper.updateUser(user, updateRequest);
        UserResponse updatedUser = userMapper.toUserResponse(userRepository.save(user));
        log.info("User info updated successfully for userId: {}", currentUserId);

        return updatedUser;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String userId = getCurrentUserId();
        log.info("Resetting password for userId: {}", userId);

        try {
            identityClient.resetPassword("Bearer " + getAccessToken(), userId,
                    Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(request.getNewPassword())
                            .build());

            log.info("Password reset successfully for userId: {}", userId);
        } catch (FeignException e) {
            log.error("FeignException during password reset for userId: {}", userId, e);
            throw errorNormalizer.handleKeyCloakException(e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        log.info("Attempting to delete user with userId: {}", userId);

        if (!userRepository.existsByUserId(userId)) {
            log.error("User not found for userId: {}", userId);
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }

        try {
            identityClient.deleteUser("Bearer " + getAccessToken(), userId);
        } catch (FeignException e) {
            log.error("FeignException during user deletion for userId: {}", userId, e);
            throw errorNormalizer.handleKeyCloakException(e);
        }

        userRepository.deleteByUserId(userId);
        log.info("User deleted successfully with userId: {}", userId);
    }

    @Override
    public UserResponse getMyInfo() {
        String userId = getCurrentUserId();

        log.info("Fetching info for current user: {}", userId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("(getMyInfo) Profile not found for userId: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        return userMapper.toUserResponse(user);
    }

    @Override
    public Page<UserResponse> getUsers(int page, int size) {
        log.info("Fetching all users with pagination: page = {}, size = {}", page, size);

        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

        log.info("Fetched all users successfully with total elements: {}", userPage.getTotalElements());

        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        List<UserResponse> users = userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
        log.info("Fetched all users successfully");

        return users;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserByUserId(String userId) {
        log.info("Fetching user with userId: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("(getUserByUserId) Profile not found for userId: {}", userId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserByProfileId(String profileId) {
        log.info("Fetching user with profileId: {}", profileId);

        User user = userRepository.findById(profileId)
                .orElseThrow(() -> {
                    log.error("(getUserByProfileId) Profile not found for profileId: {}", profileId);
                    return new AppException(ErrorCode.PROFILE_NOT_FOUND);
                });

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

    private boolean isEmailExists(String email, String userId) {
        return userRepository.findByEmail(email)
                .map(existingUser -> !existingUser.getUserId().equals(userId))
                .orElse(false);
    }

}