package com.tien.promotion.service.impl;

import com.tien.promotion.dto.ApiResponse;
import com.tien.promotion.dto.request.PromotionCreationRequest;
import com.tien.promotion.dto.request.PromotionUpdateRequest;
import com.tien.promotion.dto.response.CartItemResponse;
import com.tien.promotion.dto.response.CartResponse;
import com.tien.promotion.dto.response.PromotionResponse;
import com.tien.promotion.entity.Promotion;
import com.tien.promotion.exception.AppException;
import com.tien.promotion.exception.ErrorCode;
import com.tien.promotion.httpclient.CartClient;
import com.tien.promotion.httpclient.ProductClient;
import com.tien.promotion.httpclient.ShopClient;
import com.tien.promotion.mapper.PromotionMapper;
import com.tien.promotion.repository.PromotionRepository;
import com.tien.promotion.service.PromotionService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionServiceImpl implements PromotionService {

      PromotionMapper promotionMapper;
      PromotionRepository promotionRepository;
      ShopClient shopClient;
      ProductClient productClient;
      CartClient cartClient;

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public PromotionResponse createPromotion(PromotionCreationRequest request) {
            List<String> applicableShops = request.getConditions().getApplicableShops();
            if (applicableShops != null && !applicableShops.isEmpty()) {
                  for (String shopId : applicableShops) {
                        try {
                              ApiResponse<Boolean> shopExistsResponse = shopClient.checkIfShopExists(shopId);
                              if (shopExistsResponse == null || !shopExistsResponse.getResult()) {
                                    log.error("Shop with id {} does not exist", shopId);
                                    throw new AppException(ErrorCode.SHOP_NOT_FOUND);
                              }
                        } catch (FeignException e) {
                              log.error("FeignException occurred while checking if shop exists for shopId={} : {}",
                                      shopId, e.getMessage());
                              throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                        }
                  }
            }

            List<String> applicableProducts = request.getConditions().getApplicableProducts();
            if (applicableProducts != null && !applicableProducts.isEmpty()) {
                  for (String productId : applicableProducts) {
                        try {
                              ApiResponse<Boolean> productExistsResponse = productClient.isProductExist(productId);
                              if (productExistsResponse == null || !productExistsResponse.getResult()) {
                                    log.error("Product with id {} does not exist", productId);
                                    throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                              }
                        } catch (FeignException e) {
                              log.error("FeignException occurred while checking if product exists for productId={} : {}",
                                      productId, e.getMessage());
                              throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                        }
                  }
            }

            Promotion promotion = promotionMapper.toPromotion(request);
            promotion.setUsageCount(0);

            Promotion savedPromotion = promotionRepository.save(promotion);
            return promotionMapper.toPromotionResponse(savedPromotion);
      }

      @Override
      public void applyPromotionCode(String promoCode) {
            Promotion promotion = promotionRepository.findByPromoCode(promoCode)
                    .orElseThrow(() -> {
                          log.error("Promotion with promoCode {} not found", promoCode);
                          return new AppException(ErrorCode.PROMOTION_NOT_FOUND);
                    });

            if (promotion.getEndDate().isBefore(LocalDate.now().atStartOfDay())) {
                  log.error("Promotion with promoCode {} has expired", promoCode);
                  throw new AppException(ErrorCode.PROMOTION_EXPIRED);
            }

            if (promotion.getUsageCount() >= promotion.getUsageLimit()) {
                  log.error("Promotion with promoCode {} has reached its usage limit", promoCode);
                  throw new AppException(ErrorCode.PROMOTION_USAGE_LIMIT_REACHED);
            }

            CartResponse cartResponse;
            try {
                  cartResponse = cartClient.getMyCart().getResult();
            } catch (FeignException e) {
                  log.error("Failed to fetch cart for promoCode {}: {}", promoCode, e.getMessage());
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }

            if (cartResponse == null || cartResponse.getItems() == null || cartResponse.getItems().isEmpty()) {
                  log.error("Cart is empty or not found");
                  throw new AppException(ErrorCode.CART_NOT_FOUND);
            }

            double eligibleCartTotal = 0;
            for (CartItemResponse item : cartResponse.getItems()) {
                  String productId = item.getProductId();
                  String shopId;
                  try {
                        shopId = productClient.getShopIdByProductId(productId).getResult();
                  } catch (FeignException e) {
                        log.error("(eligibleCartTotal) Failed to fetch shop ID for productId {}: {}", productId, e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }

                  boolean isApplicableForShop = promotion.getConditions().getApplicableShops().contains(shopId);
                  boolean isApplicableForProduct = promotion.getConditions().getApplicableProducts().contains(productId);

                  if (isApplicableForShop && isApplicableForProduct) {
                        eligibleCartTotal += item.getTotalPrice() * item.getQuantity();
                  }
            }

            if (eligibleCartTotal < promotion.getConditions().getMinOrderValue()) {
                  log.error("Eligible order value is less than the minimum required for promoCode {}", promoCode);
                  throw new AppException(ErrorCode.ORDER_VALUE_TOO_LOW);
            }

            double totalDiscount = 0;

            for (CartItemResponse item : cartResponse.getItems()) {
                  String productId = item.getProductId();
                  String shopId;
                  try {
                        shopId = productClient.getShopIdByProductId(productId).getResult();
                  } catch (FeignException e) {
                        log.error("(totalDiscount) Failed to fetch shop ID for productId {}: {}", productId, e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }

                  boolean isApplicableForShop = promotion.getConditions().getApplicableShops().contains(shopId);
                  boolean isApplicableForProduct = promotion.getConditions().getApplicableProducts().contains(productId);

                  if (!isApplicableForShop || !isApplicableForProduct) {
                        log.warn("Product {} is not eligible for promoCode {} (ShopId: {}, ProductId: {})",
                                productId, promoCode, shopId, productId);
                        continue;
                  }

                  if ("fixed".equalsIgnoreCase(promotion.getType())) {
                        totalDiscount += promotion.getDiscount().getAmount();
                  } else if ("percentage".equalsIgnoreCase(promotion.getType())) {
                        totalDiscount += cartResponse.getTotal() * (promotion.getDiscount().getPercentage() / 100);
                  }
            }

            double maxDiscountAllowed = promotion.getDiscount().getMaxDiscountValue() != null ?
                    promotion.getDiscount().getMaxDiscountValue() : Double.MAX_VALUE;

            totalDiscount = Math.min(totalDiscount, maxDiscountAllowed);

            if (totalDiscount > 0) {
                  cartResponse.setTotal(cartResponse.getTotal() - totalDiscount);
                  try {
                        cartClient.updateCartTotal(cartResponse.getUsername(), cartResponse.getTotal());
                  } catch (FeignException e) {
                        log.error("Failed to update cart total for username {}: {}",
                                cartResponse.getUsername(), e.getMessage());
                        throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
                  }

                  promotion.setUsageCount(promotion.getUsageCount() + 1);
                  promotionRepository.save(promotion);
            } else {
                  log.warn("No eligible products for promoCode {}", promoCode);
                  throw new AppException(ErrorCode.NO_ELIGIBLE_PRODUCTS);
            }
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public PromotionResponse updatePromotion(String id, PromotionUpdateRequest request) {
            Promotion existingPromotion = findPromotionById(id);
            promotionMapper.updatePromotion(existingPromotion, request);

            Promotion savedPromotion = promotionRepository.save(existingPromotion);
            return promotionMapper.toPromotionResponse(savedPromotion);
      }

      @Override
      @PreAuthorize("hasRole('ADMIN')")
      public void deletePromotion(String id) {
            promotionRepository.delete(findPromotionById(id));
      }

      @Override
      public PromotionResponse getPromotionById(String id) {
            return promotionMapper.toPromotionResponse(findPromotionById(id));
      }

      @Override
      public List<PromotionResponse> getAllPromotions() {
            return promotionRepository.findAll()
                    .stream()
                    .map(promotionMapper::toPromotionResponse)
                    .toList();
      }

      private Promotion findPromotionById(String id) {
            return promotionRepository.findById(id)
                    .orElseThrow(() -> {
                          log.error("Promotion with id {} not found", id);
                          return new AppException(ErrorCode.PROMOTION_NOT_FOUND);
                    });
      }

}