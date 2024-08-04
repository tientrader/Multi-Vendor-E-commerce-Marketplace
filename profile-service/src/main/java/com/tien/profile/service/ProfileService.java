package com.tien.profile.service;

import com.tien.profile.dto.response.ProfileResponse;
import com.tien.profile.exception.AppException;
import com.tien.profile.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.tien.profile.dto.request.ProfileCreationRequest;
import com.tien.profile.entity.Profile;
import com.tien.profile.mapper.ProfileMapper;
import com.tien.profile.repository.ProfileRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    ProfileRepository profileRepository;
    ProfileMapper profileMapper;

    // Create a new user profile
    public ProfileResponse createProfile(ProfileCreationRequest request) {
        Profile profile = profileMapper.toUserProfile(request);
        profile = profileRepository.save(profile);

        return profileMapper.toUserProfileResponse(profile);
    }

    // Delete a user profile
    public void deleteProfile(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        profileRepository.delete(profile);
    }

    // Get user profile by profileId
    public ProfileResponse getProfileById(String id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toUserProfileResponse(profile);
    }

    // Get user profile by userId
    public ProfileResponse getProfileByUserId(String userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return profileMapper.toUserProfileResponse(profile);
    }

    // Get all user profiles
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toUserProfileResponse)
                .toList();
    }

}