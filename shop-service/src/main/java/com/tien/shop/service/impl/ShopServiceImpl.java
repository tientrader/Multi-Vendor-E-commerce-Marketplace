package com.tien.shop.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ShopResponse;
import com.tien.shop.entity.Shop;
import com.tien.shop.exception.AppException;
import com.tien.shop.exception.ErrorCode;
import com.tien.shop.mapper.ShopMapper;
import com.tien.shop.repository.ShopRepository;
import com.tien.shop.service.ShopService;
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
public class ShopServiceImpl implements ShopService {

      ShopRepository shopRepository;
      KafkaTemplate<String, Object> kafkaTemplate;
      ShopMapper shopMapper;

      @Override
      @Transactional
      public ShopResponse createShop(ShopCreationRequest request) {
            String username = getCurrentUsername();

            log.info("Starting shop creation for owner: {}", username);
            if (shopRepository.existsByOwnerUsername(username)) {
                  log.error("Shop creation failed: User {} already has a shop.", username);
                  throw new AppException(ErrorCode.ALREADY_HAVE_A_SHOP);
            }

            Shop shop = shopMapper.toShop(request);
            shop.setOwnerUsername(username);
            shop = shopRepository.save(shop);

            log.info("Shop created successfully for owner: {}", username);
            kafkaTemplate.send("shop-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(request.getEmail())
                    .subject("Shop Created Successfully")
                    .body("Thanks for choosing us. Wish you all the best!")
                    .build());

            return shopMapper.toShopResponse(shop);
      }

      @Override
      public ShopResponse getShopByOwnerUsername(String ownerUsername) {
            log.info("Fetching shop for owner: {}", ownerUsername);

            Shop shop = shopRepository.findByOwnerUsername(ownerUsername)
                    .orElseThrow(() -> {
                          log.error("(getShopByOwnerUsername) Shop not found for owner: {}", ownerUsername);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });
            log.info("Shop found for owner: {}", ownerUsername);

            return shopMapper.toShopResponse(shop);
      }

      @Override
      @Transactional
      public ShopResponse updateShop(ShopUpdateRequest request) {
            String username = getCurrentUsername();

            log.info("Updating shop for owner: {}", username);
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(updateShop) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            shopMapper.updateShop(shop, request);
            ShopResponse updatedShopResponse = shopMapper.toShopResponse(shopRepository.save(shop));
            log.info("Shop updated successfully for owner: {}", username);

            return updatedShopResponse;
      }

      @Override
      @Transactional
      public void deleteShop() {
            String username = getCurrentUsername();

            log.info("Deleting shop for owner: {}", username);
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(deleteShop) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            shopRepository.delete(shop);
            log.info("Shop deleted successfully for owner: {}", username);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

}