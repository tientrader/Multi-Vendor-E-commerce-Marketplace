package com.tien.user.service;

import com.tien.user.dto.request.*;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

      UserResponse register(RegistrationRequest request);

      TokenResponse socialLogin(String code);

      TokenResponse login(UserLoginRequest request);

      TokenResponse refreshToken(RefreshTokenRequest request);

      void logout();

      void forgotPassword(ForgotPasswordRequest request);

      UserResponse updateUser(String userId, UserUpdateRequest updateRequest);

      UserResponse updateMyInfo(UserUpdateRequest updateRequest);

      void resetPassword(ResetPasswordRequest request);

      void deleteUser(String userId);

      Page<UserResponse> getUsers(int page, int size);

      List<UserResponse> getAllUsers();

      UserResponse getMyInfo();

      UserResponse getUserByUserId(String userId);

      UserResponse getUserByProfileId(String profileId);

      UserResponse getUserByUsername(String username);

}