package com.tien.user.service;

import com.tien.event.dto.NotificationEvent;
import com.tien.user.dto.request.ShopCreationRequest;
import com.tien.user.dto.request.ShopUpdateRequest;
import com.tien.user.dto.response.ShopResponse;
import com.tien.user.entity.Shop;
import com.tien.user.exception.AppException;
import com.tien.user.exception.ErrorCode;
import com.tien.user.mapper.ShopMapper;
import com.tien.user.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopService {

      ShopRepository shopRepository;
      KafkaTemplate<String , Object> kafkaTemplate;
      ShopMapper shopMapper;

      public ShopResponse createShop(ShopCreationRequest request) {
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");

            if (shopRepository.existsByOwnerUsername(username)) {
                  throw new AppException(ErrorCode.ALREADY_HAVE_A_SHOP);
            }

            Shop shop = shopMapper.toShop(request);
            shop.setOwnerUsername(username);
            shop = shopRepository.save(shop);

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
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            shopMapper.updateShop(shop, request);
            shop = shopRepository.save(shop);

            return shopMapper.toShopResponse(shop);
      }

      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public void deleteShop() {
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
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