package com.tien.promotion.service;

import com.tien.promotion.dto.request.PromotionCreationRequest;
import com.tien.promotion.dto.request.PromotionUpdateRequest;
import com.tien.promotion.dto.response.PromotionResponse;

import java.util.List;

public interface PromotionService {

      PromotionResponse createPromotion(PromotionCreationRequest request);

      void applyPromotionCode(String promoCode);

      PromotionResponse updatePromotion(String id, PromotionUpdateRequest request);

      void deletePromotion(String id);

      PromotionResponse getPromotionById(String id);

      List<PromotionResponse> getAllPromotions();

}