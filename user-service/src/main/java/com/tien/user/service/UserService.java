package com.tien.user.service;

import com.tien.user.dto.request.*;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

      UserResponse register(RegistrationRequest request);

      TokenResponse login(UserLoginRequest request);

      TokenResponse refreshToken(RefreshTokenRequest request);

      void forgotPassword(ForgotPasswordRequest request);

      UserResponse updateUser(String userId, UserUpdateRequest updateRequest);

      UserResponse updateMyInfo(UserUpdateRequest updateRequest);

      void resetPassword(ResetPasswordRequest request);

      void deleteUser(String userId);

      UserResponse getMyInfo();

      Page<UserResponse> getUsers(int page, int size);

      List<UserResponse> getAllUsers();

      UserResponse getUserByUserId(String userId);

      UserResponse getUserByProfileId(String profileId);

}