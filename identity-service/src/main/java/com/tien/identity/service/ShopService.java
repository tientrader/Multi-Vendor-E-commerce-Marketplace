package com.tien.identity.service;

import com.tien.event.dto.NotificationEvent;
import com.tien.identity.constant.PredefinedRole;
import com.tien.identity.dto.request.ShopCreationRequest;
import com.tien.identity.dto.request.ShopUpdateRequest;
import com.tien.identity.dto.response.ShopResponse;
import com.tien.identity.entity.Role;
import com.tien.identity.entity.Shop;
import com.tien.identity.entity.User;
import com.tien.identity.exception.AppException;
import com.tien.identity.exception.ErrorCode;
import com.tien.identity.mapper.ShopMapper;
import com.tien.identity.repository.RoleRepository;
import com.tien.identity.repository.ShopRepository;
import com.tien.identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {

      ShopRepository shopRepository;
      UserRepository userRepository;
      RoleRepository roleRepository;
      KafkaTemplate<String , Object> kafkaTemplate;
      ShopMapper shopMapper;

      @Transactional
      public ShopResponse createShop(ShopCreationRequest request) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (shopRepository.existsByOwnerUsername(username)) {
                  throw new AppException(ErrorCode.ALREADY_HAVE_A_SHOP);
            }

            Shop shop = shopMapper.toShop(request);
            shop.setOwnerUsername(username);

            shop = shopRepository.save(shop);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Set<Role> roles = new HashSet<>(user.getRoles());
            roleRepository.findById(PredefinedRole.SELLER_ROLE)
                    .ifPresent(roles::add);
            user.setRoles(roles);
            userRepository.save(user);

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Shop Created Successfully")
                    .body("Thanks for choosing us. Wish you all the best!")
                    .build();

            kafkaTemplate.send("shop-created-successfully", notificationEvent);

            return shopMapper.toShopResponse(shop);
      }

      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public ShopResponse updateShop(ShopUpdateRequest request) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            shopMapper.updateShop(shop, request);
            shop = shopRepository.save(shop);

            return shopMapper.toShopResponse(shop);
      }

      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public void deleteShop() {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            shopRepository.delete(shop);
      }

      public ShopResponse getShopByOwnerUsername(String ownerUsername) {
            Shop shop = shopRepository.findByOwnerUsername(ownerUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            return shopMapper.toShopResponse(shop);
      }

}