package com.tien.profile.service;

import com.tien.profile.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.tien.profile.dto.request.ProfileCreationRequest;
import com.tien.profile.dto.response.UserProfileResponse;
import com.tien.profile.entity.UserProfile;
import com.tien.profile.mapper.UserProfileMapper;
import com.tien.profile.repository.UserProfileRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tien.profile.exception.ErrorCode.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {

    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    // Tạo thông tin User
    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    // Xem thông tin User bằng ID
    public UserProfileResponse getProfileById(String id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new AppException(PROFILE_NOT_FOUND));

        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    // Xem thông tin User bằng UserID
    public UserProfileResponse getProfileByUserId(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(PROFILE_NOT_FOUND));

        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    // Xem thông tin của tất cả User dành cho Admin
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toUserProfileReponse).toList();
    }

    // Xoá thông tin User
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProfile(String userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(PROFILE_NOT_FOUND));

        userProfileRepository.delete(userProfile);
    }

}