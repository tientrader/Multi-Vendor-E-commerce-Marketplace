package com.tien.user.mapper;

import com.tien.user.dto.request.RegistrationRequest;
import com.tien.user.dto.response.UserResponse;
import com.tien.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegistrationRequest request);

    UserResponse toUserResponse(User user);

}