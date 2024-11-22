package com.tien.shop.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.*;
import com.tien.shop.entity.Shop;
import com.tien.shop.exception.AppException;
import com.tien.shop.exception.ErrorCode;
import com.tien.shop.httpclient.ProductClient;
import com.tien.shop.mapper.ShopMapper;
import com.tien.shop.repository.ShopRepository;
import com.tien.shop.service.ShopService;
import feign.FeignException;
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

import java.util.*;
import java.util.stream.Collectors;

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
            Shop shop = findShopByOwnerUsername(getCurrentUsername());
            shopMapper.updateShop(shop, request);
            return shopMapper.toShopResponse(shopRepository.save(shop));
      }

      @Override
      @Transactional
      public void deleteShop() {
            shopRepository.delete(findShopByOwnerUsername(getCurrentUsername()));
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public SalesReportResponse generateSalesReport(String shopId, String startDate, String endDate) {
            List<ProductResponse> products;
            try {
                  products = productClient.getProductsByShopId(shopId).getResult();
            } catch (FeignException e) {
                  log.error("Error fetching products for shopId {}: {}", shopId, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            return generateSalesData(products, startDate, endDate);
      }

      @Override
      public SalesReportResponse getMySalesReport(String startDate, String endDate) {
            String username = getCurrentUsername();
            Shop shop = findShopByOwnerUsername(username);

            List<ProductResponse> products;
            try {
                  products = productClient.getProductsByShopId(shop.getId()).getResult();
            } catch (FeignException e) {
                  log.error("(User) Error fetching products for shopId {}: {}", shop.getId(), e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            return generateSalesData(products, startDate, endDate);
      }

      @Override
      public ShopResponse getShopByOwnerUsername(String ownerUsername) {
            return shopMapper.toShopResponse(findShopByOwnerUsername(ownerUsername));
      }

      @Override
      public String getOwnerUsernameByShopId(String shopId) {
            return findShopById(shopId).getOwnerUsername();
      }

      @Override
      public boolean checkIfShopExists(String shopId) {
            return shopRepository.existsById(shopId);
      }

      private SalesReportResponse generateSalesData(List<ProductResponse> products, String startDate, String endDate) {
            if (products == null || products.isEmpty()) {
                  return new SalesReportResponse(
                          0.0,
                          0,
                          Collections.emptyList(),
                          "No Product",
                          0.0,
                          "No Product",
                          startDate, endDate
                  );
            }

            double totalRevenue = 0.0;
            int totalItemsSold = 0;
            Map<String, Integer> productSalesCount = new HashMap<>();
            String topSellingProduct = "";
            int highestSoldQuantity = 0;
            double highestRevenueProductRevenue = 0.0;
            String highestRevenueProduct = "";

            for (ProductResponse product : products) {
                  double productRevenue = 0.0;
                  int productTotalSold = 0;

                  for (ProductVariantResponse variant : product.getVariants()) {
                        int soldQuantity = variant.getSoldQuantity();
                        double price = variant.getPrice();
                        productRevenue += price * soldQuantity;
                        productTotalSold += soldQuantity;
                  }

                  totalRevenue += productRevenue;
                  totalItemsSold += productTotalSold;
                  productSalesCount.put(product.getName(), productTotalSold);

                  if (productTotalSold > highestSoldQuantity) {
                        highestSoldQuantity = productTotalSold;
                        topSellingProduct = product.getName();
                  }

                  if (productRevenue > highestRevenueProductRevenue) {
                        highestRevenueProductRevenue = productRevenue;
                        highestRevenueProduct = product.getName();
                  }
            }

            List<Map.Entry<String, Integer>> sortedProductSales = productSalesCount.entrySet()
                    .stream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                    .collect(Collectors.toList());

            return new SalesReportResponse(
                    totalRevenue,
                    totalItemsSold,
                    sortedProductSales,
                    topSellingProduct,
                    highestRevenueProductRevenue,
                    highestRevenueProduct,
                    startDate,
                    endDate
            );
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