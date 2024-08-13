package com.tien.shop.service;

import com.tien.event.dto.NotificationEvent;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ShopResponse;
import com.tien.shop.entity.Shop;
import com.tien.shop.exception.AppException;
import com.tien.shop.exception.ErrorCode;
import com.tien.shop.mapper.ShopMapper;
import com.tien.shop.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      @Transactional
      public ShopResponse createShop(ShopCreationRequest request) {
            String username = getCurrentUsername();
            if (shopRepository.existsByOwnerUsername(username)) {
                  throw new AppException(ErrorCode.ALREADY_HAVE_A_SHOP);
            }
            Shop shop = shopMapper.toShop(request);
            shop.setOwnerUsername(username);
            shop = shopRepository.save(shop);

            kafkaTemplate.send("shop-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Shop Created Successfully")
                    .body("Thanks for choosing us. Wish you all the best!")
                    .build());

            return shopMapper.toShopResponse(shop);
      }

      @Transactional
      public ShopResponse updateShop(ShopUpdateRequest request) {
            String username = getCurrentUsername();
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            shopMapper.updateShop(shop, request);
            return shopMapper.toShopResponse(shopRepository.save(shop));
      }

      @Transactional
      public void deleteShop() {
            String username = getCurrentUsername();
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