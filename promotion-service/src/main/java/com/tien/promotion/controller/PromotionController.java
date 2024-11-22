package com.tien.promotion.controller;

import com.tien.promotion.dto.ApiResponse;
import com.tien.promotion.dto.request.PromotionCreationRequest;
import com.tien.promotion.dto.request.PromotionUpdateRequest;
import com.tien.promotion.dto.response.PromotionResponse;
import com.tien.promotion.service.PromotionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromotionController {

      PromotionService promotionService;

      @PostMapping("/create")
      public ApiResponse<PromotionResponse> createPromotion(@RequestBody PromotionCreationRequest request) {
            return ApiResponse.<PromotionResponse>builder()
                    .result(promotionService.createPromotion(request))
                    .build();
      }

      @PostMapping("/apply")
      public ApiResponse<Void> applyPromotionCode(@RequestParam String promoCode) {
            promotionService.applyPromotionCode(promoCode);
            return ApiResponse.<Void>builder()
                    .message("Promotion code applied successfully!")
                    .build();
      }

      @PutMapping("/{id}")
      public ApiResponse<PromotionResponse> updatePromotion(@PathVariable String id, @RequestBody PromotionUpdateRequest request) {
            return ApiResponse.<PromotionResponse>builder()
                    .result(promotionService.updatePromotion(id, request))
                    .build();
      }

      @DeleteMapping("/{id}")
      public ApiResponse<Void> deletePromotion(@PathVariable String id) {
            promotionService.deletePromotion(id);
            return ApiResponse.<Void>builder()
                    .message("Delete successfully!")
                    .build();
      }

      @GetMapping("/{id}")
      public ApiResponse<PromotionResponse> getPromotionById(@PathVariable String id) {
            return ApiResponse.<PromotionResponse>builder()
                    .result(promotionService.getPromotionById(id))
                    .build();
      }

      @GetMapping("/all")
      public ApiResponse<List<PromotionResponse>> getAllPromotions() {
            return ApiResponse.<List<PromotionResponse>>builder()
                    .result(promotionService.getAllPromotions())
                    .build();
      }

}