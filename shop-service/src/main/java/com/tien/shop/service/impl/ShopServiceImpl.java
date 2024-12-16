package com.tien.shop.service.impl;

import com.tien.event.dto.NotificationEvent;
import com.tien.shop.dto.request.ShopCreationRequest;
import com.tien.shop.dto.request.ShopUpdateRequest;
import com.tien.shop.dto.response.*;
import com.tien.shop.entity.Shop;
import com.tien.shop.exception.AppException;
import com.tien.shop.exception.ErrorCode;
import com.tien.shop.httpclient.ProductClient;
import com.tien.shop.httpclient.response.ProductResponse;
import com.tien.shop.httpclient.response.ProductVariantResponse;
import com.tien.shop.mapper.ShopMapper;
import com.tien.shop.repository.ShopRepository;
import com.tien.shop.service.ShopService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
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
            String email = getCurrentEmail();

            if (shopRepository.existsByOwnerUsername(username)) {
                  log.error("User {} already has a shop", username);
                  throw new AppException(ErrorCode.ALREADY_HAVE_A_SHOP);
            }

            Shop shop = shopMapper.toShop(request);
            shop.setOwnerUsername(username);
            shop.setEmail(email);
            shop = shopRepository.save(shop);

            kafkaTemplate.send("shop-created-successful", NotificationEvent.builder()
                    .channel("EMAIL")
                    .recipient(email)
                    .subject("Shop Created Successfully")
                    .body("Thanks for choosing us. Wish you all the best!")
                    .build());

            return shopMapper.toShopResponse(shop);
      }

      @Override
      @Transactional
      public ShopResponse updateShop(ShopUpdateRequest request) {
            String currentUsername = getCurrentUsername();
            Shop shop = shopRepository.findByOwnerUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            shopMapper.updateShop(shop, request);
            return shopMapper.toShopResponse(shopRepository.save(shop));
      }

      @Override
      @Transactional
      public void deleteShop() {
            String currentUsername = getCurrentUsername();
            Shop shop = shopRepository.findByOwnerUsername(currentUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            shopRepository.delete(shop);
      }

      @Override
      @Transactional
      public SalesReportResponse generateSalesReport(String shopId, String startDate, String endDate) {
            List<ProductResponse> products;
            try {
                  products = productClient.getProductsByShopId(shopId).getResult();
            } catch (FeignException e) {
                  log.error("Error fetching products for shopId {}: {}", shopId, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

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
                    .toList();

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

      @Override
      public SalesReportResponse getMySalesReport(String startDate, String endDate) {
            String username = getCurrentUsername();
            Shop shop = shopRepository.findByOwnerUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

            List<ProductResponse> products;
            try {
                  products = productClient.getProductsByShopId(shop.getId()).getResult();
            } catch (FeignException e) {
                  log.error("(User) Error fetching products for shopId {}: {}", shop.getId(), e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

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

      @Override
      public Page<ShopResponse> searchShops(String keyword, int page, int size) {
            PageRequest pageRequest = PageRequest.of(page, size);
            Page<Shop> shops = shopRepository.searchShops(keyword, pageRequest);
            return shops.map(shopMapper::toShopResponse);
      }

      @Override
      public ShopResponse getShopByOwnerUsername(String ownerUsername) {
            Shop shop = shopRepository.findByOwnerUsername(ownerUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            return shopMapper.toShopResponse(shop);
      }

      @Override
      public String getOwnerUsernameByShopId(String shopId) {
            return shopRepository.findById(shopId)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND))
                    .getOwnerUsername();
      }

      @Override
      public boolean checkIfShopExists(String shopId) {
            return shopRepository.existsById(shopId);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private String getCurrentEmail() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("email");
      }

}