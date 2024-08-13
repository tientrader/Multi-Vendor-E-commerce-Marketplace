package com.tien.user.service;

import com.tien.event.dto.NotificationEvent;
import com.tien.user.dto.identity.Credential;
import com.tien.user.dto.identity.TokenExchangeParam;
import com.tien.user.dto.identity.UserCreationParam;
import com.tien.user.dto.request.RegistrationRequest;
import com.tien.user.dto.request.UserUpdateRequest;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.entity.User;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.exception.ErrorNormalizer;
import com.tien.user.httpclient.IdentityClient;
import com.tien.user.mapper.UserMapper;
import com.tien.user.repository.UserRepository;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

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

    public UserResponse register(RegistrationRequest request) {
        try {
            String token = getAccessToken();
            log.info("TokenInfo {}", token);

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
            log.info("UserId {}", userId);

            User user = userMapper.toUser(request);
            user.setUserId(userId);
            user = userRepository.save(user);

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Welcome to TienProApp")
                    .body("Hello, " + request.getUsername())
                    .build();

            kafkaTemplate.send("register-successful", notificationEvent);
            return userMapper.toUserResponse(user);
        } catch (FeignException e) {
            handleFeignException(e);
        }
        return null;
    }

    public UserResponse getMyInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String userId, UserUpdateRequest updateRequest) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        try {
            identityClient.updateUser("Bearer " + getAccessToken(), userId, updateRequest);
        } catch (FeignException e) {
            handleFeignException(e);
        }

        userMapper.updateUser(user, updateRequest);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public void resetPassword(String newPassword) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            identityClient.resetPassword("Bearer " + getAccessToken(), userId,
                    Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(newPassword)
                            .build());
        } catch (FeignException e) {
            handleFeignException(e);
        }
    }

    public void deleteUser(String userId) {
        try {
            identityClient.deleteUser("Bearer " + getAccessToken(), userId);
            userRepository.deleteByUserId(userId);
        } catch (FeignException e) {
            handleFeignException(e);
        }
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

    private void handleFeignException(FeignException exception) {
        throw errorNormalizer.handleKeyCloakException(exception);
    }

}