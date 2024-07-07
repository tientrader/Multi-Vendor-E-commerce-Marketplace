package com.tien.identity.mapper;

import com.tien.identity.dto.request.UserInfoUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tien.identity.dto.request.UserCreationRequest;
import com.tien.identity.dto.request.UserUpdateRequest;
import com.tien.identity.dto.response.UserResponse;
import com.tien.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "noPassword", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UserInfoUpdateRequest request);

}