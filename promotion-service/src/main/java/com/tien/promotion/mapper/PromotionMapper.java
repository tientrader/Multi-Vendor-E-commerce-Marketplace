package com.tien.promotion.mapper;

import com.tien.promotion.dto.request.PromotionCreationRequest;
import com.tien.promotion.dto.request.PromotionUpdateRequest;
import com.tien.promotion.dto.response.PromotionResponse;
import com.tien.promotion.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

      Promotion toPromotion(PromotionCreationRequest request);

      PromotionResponse toPromotionResponse(Promotion promotion);

      void updatePromotion(@MappingTarget Promotion promotion, PromotionUpdateRequest request);

}