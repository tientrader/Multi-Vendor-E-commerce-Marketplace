package com.tien.user.mapper;

import com.tien.user.dto.request.VIPUserRequest;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VIPUserMapper {

      User vipUserRequestToUser(VIPUserRequest request);

      VIPUserResponse userToVipUserResponse(User user);

}