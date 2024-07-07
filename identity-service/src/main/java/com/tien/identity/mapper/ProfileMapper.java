package com.tien.identity.mapper;

import org.mapstruct.Mapper;

import com.tien.identity.dto.request.ProfileCreationRequest;
import com.tien.identity.dto.request.UserCreationRequest;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "userId", ignore = true)
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);

}