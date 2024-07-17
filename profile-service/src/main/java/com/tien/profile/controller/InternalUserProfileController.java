package com.tien.profile.controller;

import com.tien.profile.dto.ApiResponse;
import com.tien.profile.dto.request.ProfileCreationRequest;
import com.tien.profile.dto.response.UserProfileResponse;
import com.tien.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalUserProfileController {

    UserProfileService userProfileService;

    @PostMapping("/internal/users")
    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createProfile(request))
                .build();
    }

    @DeleteMapping("/internal/users/{userId}")
    ApiResponse<String> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ApiResponse.<String>builder()
                .result("Profile deleted successfully!")
                .build();
    }

}