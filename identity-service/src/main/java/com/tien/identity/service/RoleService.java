package com.tien.identity.service;

import java.util.HashSet;
import java.util.List;

import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tien.identity.dto.request.RoleRequest;
import com.tien.identity.dto.response.RoleResponse;
import com.tien.identity.mapper.RoleMapper;
import com.tien.identity.repository.PermissionRepository;
import com.tien.identity.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    // Create role for User
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    // Delete role based on the name of the role
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteRole(String role) {
        if (!roleRepository.existsById(role)) {throw new AppException(ErrorCode.ROLE_NOT_FOUND);}
        roleRepository.deleteById(role);
    }

    // View all existing roles
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

}