package com.tien.identity.service;

import java.util.List;

import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tien.identity.dto.request.PermissionRequest;
import com.tien.identity.dto.response.PermissionResponse;
import com.tien.identity.entity.Permission;
import com.tien.identity.mapper.PermissionMapper;
import com.tien.identity.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    // Tạo permission cho User
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    // Xem tất cả các permission hiện có
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::toPermissionResponse).toList();
    }

    // Xoá permission dựa trên tên của permission
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePermission(String permission) {
        if (!permissionRepository.existsById(permission)) {throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);}
        permissionRepository.deleteById(permission);
    }

}