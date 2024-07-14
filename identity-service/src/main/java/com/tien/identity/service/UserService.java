package com.tien.identity.service;

import java.util.HashSet;
import java.util.List;

import com.tien.identity.dto.request.UserInfoUpdateRequest;
import com.tien.identity.dto.request.PasswordCreationRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tien.identity.constant.PredefinedRole;
import com.tien.identity.dto.request.UserCreationRequest;
import com.tien.identity.dto.request.UserUpdateRequest;
import com.tien.identity.dto.response.UserResponse;
import com.tien.identity.entity.Role;
import com.tien.identity.entity.User;
import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import com.tien.identity.mapper.ProfileMapper;
import com.tien.identity.mapper.UserMapper;
import com.tien.identity.repository.RoleRepository;
import com.tien.identity.repository.UserRepository;
import com.tien.identity.httpclient.ProfileClient;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    ProfileMapper profileMapper;
    PasswordEncoder passwordEncoder;
    ProfileClient profileClient;
    KafkaTemplate<String , String> kafkaTemplate;

    // Tạo tài khoản User
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        // Mã hoá password
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gán role cho User
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        user = userRepository.save(user);

        // Tạo profile ở Profile Service
        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());
        profileClient.createProfile(profileRequest);

        // Gửi message onboard User thành công cho Notification Service
        kafkaTemplate.send("onboard-successful", "Welcome " + user.getUsername() + " to TienApp!");

        return userMapper.toUserResponse(user);
    }

    // Tạo password cho User đăng nhập bằng Gmail
    @Transactional
    public void createPassword(PasswordCreationRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (StringUtils.hasText(user.getPassword())) throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    // Chỉnh sửa thông tin của chính User dựa trên token ngoại trừ role
    @Transactional
    public UserResponse updateInfo(UserInfoUpdateRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Chỉnh sửa tất cả thông tin của chính User bằng ID dành cho Admin
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // Xoá tài khoản của User dành cho Admin
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.deleteById(id);
        profileClient.deleteProfile(id);
    }

    // Xem thông tin của chính User dựa trên token
    public UserResponse getMyInfo() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        return userResponse;
    }

    // Xem thông tin của User bằng ID
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    // Xem thông tin của tất cả User
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

}