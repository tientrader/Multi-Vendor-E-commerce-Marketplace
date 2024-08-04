package com.tien.profile.mapper;

import com.tien.profile.dto.response.ProfileResponse;
import com.tien.profile.entity.Profile;
import com.tien.profile.dto.request.ProfileCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    Profile toUserProfile(ProfileCreationRequest request);

    ProfileResponse toUserProfileResponse(Profile entity);

}