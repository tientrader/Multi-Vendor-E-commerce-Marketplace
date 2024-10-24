package com.tien.user.mapper;

import com.tien.user.dto.identity.TokenExchangeResponse;
import com.tien.user.dto.request.RegistrationRequest;
import com.tien.user.dto.request.UserUpdateRequest;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

      User toUser(RegistrationRequest request);

      UserResponse toUserResponse(User user);

      void updateUser(@MappingTarget User user, UserUpdateRequest request);

      TokenResponse toUserLoginResponse(TokenExchangeResponse tokenExchangeResponse);

}