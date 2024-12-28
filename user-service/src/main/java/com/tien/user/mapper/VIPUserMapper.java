package com.tien.user.mapper;

import com.tien.user.dto.request.VIPUserRequestWithSession;
import com.tien.user.dto.response.VIPUserResponse;
import com.tien.user.dto.response.VIPUserResponseWithSession;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VIPUserMapper {

      VIPUserResponse userToVipUserResponse(User user);

      User vipUserRequestWithSessionToUser(VIPUserRequestWithSession request);

      VIPUserResponseWithSession userToVipUserResponseWithSession(User user);

}