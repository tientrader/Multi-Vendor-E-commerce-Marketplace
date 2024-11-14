package com.tien.shop.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.ProductResponse;
import com.tien.shop.dto.response.ProductVariantResponse;
import com.tien.shop.dto.response.ShopResponse;
import com.tien.shop.entity.Shop;
import com.tien.shop.exception.AppException;
import com.tien.shop.exception.ErrorCode;
import com.tien.shop.httpclient.ProductClient;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopServiceImpl implements ShopService {

      ShopRepository shopRepository;
      ProductClient productClient;
      KafkaTemplate<String, Object> kafkaTemplate;
      ShopMapper shopMapper;

      @Override
      @Transactional
      public ShopResponse createShop(ShopCreationRequest request) {
            String username = getCurrentUsername();

            if (shopRepository.existsByOwnerUsername(username)) {
                  log.error("User {} already has a shop", username);
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

      @Override
      @Transactional
      public ShopResponse updateShop(ShopUpdateRequest request) {
            String username = getCurrentUsername();
            Shop shop = findShopByOwnerUsername(username);
            shopMapper.updateShop(shop, request);
            return shopMapper.toShopResponse(shopRepository.save(shop));
      }

      @Override
      @Transactional
      public void deleteShop() {
            shopRepository.delete(findShopByOwnerUsername(getCurrentUsername()));
      }

      @Override
      public double calculateRevenueForShop(String shopId) {
            List<ProductResponse> products = productClient.getProductsByShopId(shopId).getResult();
            double totalRevenue = 0.0;

            for (ProductResponse product : products) {
                  double productRevenue = 0.0;
                  for (ProductVariantResponse variant : product.getVariants()) {
                        int soldQuantity = variant.getSoldQuantity();
                        double price = variant.getPrice();
                        productRevenue += price * soldQuantity;
                  }
                  totalRevenue += productRevenue;
            }

            return totalRevenue;
      }

      @Override
      public ShopResponse getShopByOwnerUsername(String ownerUsername) {
            return shopMapper.toShopResponse(findShopByOwnerUsername(ownerUsername));
      }

      @Override
      public String getOwnerUsernameByShopId(String shopId) {
            return findShopById(shopId).getOwnerUsername();
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Shop findShopByOwnerUsername(String username) {
            return shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("Shop not found for user {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });
      }

      private Shop findShopById(String shopId) {
            return shopRepository.findById(shopId)
                    .orElseThrow(() -> {
                          log.error("Shop not found for ID {}", shopId);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });
      }

}