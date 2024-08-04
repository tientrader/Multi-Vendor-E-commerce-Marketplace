package com.tien.profile.controller;

import com.tien.profile.dto.ApiResponse;
import com.tien.profile.dto.request.ProfileCreationRequest;
import com.tien.profile.dto.response.ProfileResponse;
import com.tien.profile.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalProfileController {

    ProfileService profileService;

    @PostMapping("/internal/users")
    ApiResponse<ProfileResponse> createProfile(@RequestBody ProfileCreationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.createProfile(request))
                .build();
    }

    @DeleteMapping("/internal/users/{userId}")
    ApiResponse<String> deleteProfile(@PathVariable String userId) {
        profileService.deleteProfile(userId);
        return ApiResponse.<String>builder()
                .result("Profile deleted successfully!")
                .build();
    }

}