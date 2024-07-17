package com.tien.identity.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import com.tien.identity.dto.request.PermissionRequest;
import com.tien.identity.dto.response.PermissionResponse;
import com.tien.identity.entity.Permission;
import com.tien.identity.mapper.PermissionMapper;
import com.tien.identity.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    // Create permission for User
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    // Delete permission based on the name of the permission
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePermission(String permission) {
        if (!permissionRepository.existsById(permission)) {throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);}
        permissionRepository.deleteById(permission);
    }

    // View all existing permissions
    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::toPermissionResponse).toList();
    }

}