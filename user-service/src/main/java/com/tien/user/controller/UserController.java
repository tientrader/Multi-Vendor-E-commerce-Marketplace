package com.tien.user.controller;

import com.tien.user.dto.ApiResponse;
import com.tien.user.dto.request.*;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/auth/register")
    ApiResponse<UserResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.register(request))
                .build();
    }

    @PostMapping("/auth/login")
    ApiResponse<TokenResponse> login(@RequestBody @Valid UserLoginRequest request) {
        return ApiResponse.<TokenResponse>builder()
                .result(userService.login(request))
                .build();
    }

    @PostMapping("/auth/refresh")
    ApiResponse<TokenResponse> refresh(@RequestBody TokenResponse response) {
        return ApiResponse.<TokenResponse>builder()
                .result(userService.refreshToken(response.getRefreshToken()))
                .build();
    }

    @PostMapping("/auth/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message("Reset password email sent successfully")
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping
    ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(page, size))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserByUserId(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUserId(userId))
                .build();
    }

    @GetMapping("/profile/{profileId}")
    ApiResponse<UserResponse> getUserByProfileId(@PathVariable String profileId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByProfileId(profileId))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUserProfile(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PutMapping("/my-info")
    ApiResponse<UserResponse> updateMyInfo(@RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMyInfo(request))
                .build();
    }

    @PutMapping("/reset-password")
    ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password reset successfully")
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder()
                .message("Deleted successfully")
                .build();
    }

}