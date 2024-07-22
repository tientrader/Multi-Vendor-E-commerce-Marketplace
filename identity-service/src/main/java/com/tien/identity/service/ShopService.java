package com.tien.identity.service;

import com.tien.identity.constant.PredefinedRole;
import com.tien.identity.dto.request.ShopCreationRequest;
import com.tien.identity.dto.response.ShopResponse;
import com.tien.identity.entity.Role;
import com.tien.identity.entity.Shop;
import com.tien.identity.entity.User;
import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import com.tien.identity.repository.RoleRepository;
import com.tien.identity.repository.ShopRepository;
import com.tien.identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {

      ShopRepository shopRepository;
      UserRepository userRepository;
      RoleRepository roleRepository;

      @Transactional
      public ShopResponse createShop(ShopCreationRequest request) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // Tạo shop mới
            Shop shop = new Shop();
            shop.setName(request.getName());
            shop.setOwnerUsername(username); // Đặt tên người dùng làm chủ sở hữu của shop

            // Lưu shop vào cơ sở dữ liệu
            try {
                  shop = shopRepository.save(shop);
                  log.info("Shop created successfully with ID '{}'", shop.getId());
            } catch (Exception e) {
                  log.error("Error saving shop: ", e);
                  throw new RuntimeException("Error saving shop", e);
            }

            // Cập nhật vai trò của người dùng thành "seller"
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                          log.error("User with username '{}' does not exist", username);
                          return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });

            // Cập nhật vai trò của người dùng
            try {
                  Set<Role> roles = new HashSet<>(user.getRoles());
                  roleRepository.findById(PredefinedRole.SELLER_ROLE)
                          .ifPresent(role -> {
                                roles.add(role);
                                log.info("Role '{}' added to user '{}'", PredefinedRole.SELLER_ROLE, username);
                          });
                  user.setRoles(roles);
                  userRepository.save(user);
                  log.info("User roles updated successfully");
            } catch (Exception e) {
                  log.error("Error updating user roles: ", e);
                  throw new RuntimeException("Error updating user roles", e);
            }

            // Trả về thông tin của shop đã tạo
            log.info("Returning ShopResponse with ID '{}' and name '{}'", shop.getId(), shop.getName());
            return new ShopResponse(shop.getId(), shop.getName(), shop.getOwnerUsername());
      }

}