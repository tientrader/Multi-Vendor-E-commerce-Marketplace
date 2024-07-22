package com.tien.identity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tien.identity.dto.ApiResponse;
import com.tien.identity.dto.request.RoleRequest;
import com.tien.identity.dto.response.RoleResponse;
import com.tien.identity.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping("/roles")
    ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/roles/{role}")
    ApiResponse<String> deleteRole(@PathVariable String role) {
        roleService.deleteRole(role);
        return ApiResponse.<String>builder()
                .result("Role has been deleted")
                .build();
    }

}