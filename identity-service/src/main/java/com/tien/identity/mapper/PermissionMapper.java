package com.tien.identity.mapper;

import org.mapstruct.Mapper;

import com.tien.identity.dto.request.PermissionRequest;
import com.tien.identity.dto.response.PermissionResponse;
import com.tien.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

}