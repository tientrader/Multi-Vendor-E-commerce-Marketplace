package com.tien.profile.controller;

import com.tien.profile.dto.ApiResponse;
import com.tien.profile.dto.response.ProfileResponse;
import com.tien.profile.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/{profileId}")
    ApiResponse<ProfileResponse> getProfileById(@PathVariable String profileId) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getProfileById(profileId))
                .build();
    }

    @GetMapping("/users/{userId}")
    ApiResponse<ProfileResponse> getProfileByUserId(@PathVariable String userId) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getProfileByUserId(userId))
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllProfiles())
                .build();
    }

}