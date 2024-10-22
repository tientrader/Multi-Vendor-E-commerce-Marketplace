package com.tien.user.mapper;

import com.tien.user.dto.identity.TokenExchangeResponse;
import com.tien.user.dto.request.RegistrationRequest;
import com.tien.user.dto.request.UserUpdateRequest;
import com.tien.user.dto.response.TokenResponse;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

      @Mapping(target = "vipStatus", ignore = true)
      @Mapping(target = "vipStartDate", ignore = true)
      @Mapping(target = "vipEndDate", ignore = true)
      @Mapping(target = "stripeSubscriptionId", ignore = true)
      @Mapping(target = "userId", ignore = true)
      @Mapping(target = "profileId", ignore = true)
      User toUser(RegistrationRequest request);

      UserResponse toUserResponse(User user);

      @Mapping(target = "phoneNumber", ignore = true)
      @Mapping(target = "email", ignore = true)
      @Mapping(target = "vipStatus", ignore = true)
      @Mapping(target = "vipStartDate", ignore = true)
      @Mapping(target = "vipEndDate", ignore = true)
      @Mapping(target = "stripeSubscriptionId", ignore = true)
      @Mapping(target = "username", ignore = true)
      @Mapping(target = "userId", ignore = true)
      @Mapping(target = "profileId", ignore = true)
      @Mapping(target = "dob", ignore = true)
      void updateUser(@MappingTarget User user, UserUpdateRequest request);

      TokenResponse toUserLoginResponse(TokenExchangeResponse tokenExchangeResponse);

}