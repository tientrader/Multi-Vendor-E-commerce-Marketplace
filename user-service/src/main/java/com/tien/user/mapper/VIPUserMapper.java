package com.tien.user.mapper;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VIPUserMapper {

      @Mapping(target = "vipStatus", ignore = true)
      @Mapping(target = "userId", ignore = true)
      @Mapping(target = "profileId", ignore = true)
      @Mapping(target = "lastName", ignore = true)
      @Mapping(target = "firstName", ignore = true)
      @Mapping(target = "dob", ignore = true)
      @Mapping(target = "email", source = "request.email")
      @Mapping(target = "username", source = "request.username")
      @Mapping(target = "vipStartDate", ignore = true)
      @Mapping(target = "vipEndDate", ignore = true)
      @Mapping(target = "stripeSubscriptionId", ignore = true)
      User vipUserRequestToUser(VIPUserRequest request);

      @Mapping(target = "vipStartDate", source = "user.vipStartDate")
      @Mapping(target = "vipEndDate", source = "user.vipEndDate")
      VIPUserResponse userToVipUserResponse(User user);

}