package com.tien.profile.mapper;

import com.tien.profile.dto.response.UserProfileResponse;
import com.tien.profile.entity.UserProfile;
import com.tien.profile.dto.request.ProfileCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);

}