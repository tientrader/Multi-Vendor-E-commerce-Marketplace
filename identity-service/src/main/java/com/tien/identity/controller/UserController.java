package com.tien.identity.controller;

import java.util.List;

import com.tien.identity.dto.request.UserInfoUpdateRequest;
import com.tien.identity.dto.request.PasswordCreationRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.tien.identity.dto.ApiResponse;
import com.tien.identity.dto.request.UserCreationRequest;
import com.tien.identity.dto.request.UserUpdateRequest;
import com.tien.identity.dto.response.UserResponse;
import com.tien.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/users/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/users/create-password")
    ApiResponse<Void> createPassword(@RequestBody @Valid PasswordCreationRequest request) {
        userService.createPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password has been created, you could use it to login!")
                .build();
    }

    @PutMapping("/users/my-info")
    ApiResponse<UserResponse> updateInfo(@RequestBody @Valid UserInfoUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateInfo(request))
                .build();
    }

    @PutMapping("/users/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/users/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @GetMapping("/users/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/users/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

}