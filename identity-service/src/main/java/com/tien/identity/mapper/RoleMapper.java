package com.tien.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tien.identity.dto.request.RoleRequest;
import com.tien.identity.dto.response.RoleResponse;
import com.tien.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

}