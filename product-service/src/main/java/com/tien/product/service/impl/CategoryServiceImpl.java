package com.tien.product.service.impl;

import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.httpclient.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.exception.ErrorCode;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.CategoryMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import com.tien.product.exception.AppException;
import com.tien.product.service.CategoryService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

      CategoryRepository categoryRepository;
      CategoryMapper categoryMapper;
      ShopClient shopClient;

      @Override
      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Category category = categoryMapper.toCategory(request);
            category.setShopId(shopResponse.getId());

            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryResponse(savedCategory);
      }

      @Override
      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Category category = findCategoryById(categoryId);
            validateCategoryOwnership(category, shopResponse.getId());
            categoryMapper.updateCategory(category, request);

            Category updatedCategory = categoryRepository.save(category);
            return categoryMapper.toCategoryResponse(updatedCategory);
      }

      @Override
      @Transactional
      public void deleteCategory(String categoryId) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            Category category = findCategoryById(categoryId);
            validateCategoryOwnership(category, shopResponse.getId());

            categoryRepository.deleteById(categoryId);
      }

      @Override
      public List<CategoryResponse> getAllCategories() {
            return categoryMapper.toCategoryResponses(categoryRepository.findAll());
      }

      @Override
      public CategoryResponse getCategoryById(String categoryId) {
            return categoryMapper.toCategoryResponse(findCategoryById(categoryId));
      }

      @Override
      public List<CategoryResponse> getCategoriesByShopId(String shopId) {
            return categoryMapper.toCategoryResponses(categoryRepository.findByShopId(shopId));
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Category findCategoryById(String categoryId) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
      }

      private void validateCategoryOwnership(Category category, String shopId) {
            if (!category.getShopId().equals(shopId)) {
                  log.error("User is unauthorized to access category with ID {} for shop ID {}", category.getId(), shopId);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            try {
                  ApiResponse<ShopResponse> response = shopClient.getShopByOwnerUsername(username);
                  return Optional.ofNullable(response.getResult())
                          .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            } catch (FeignException e) {
                  log.error("Error calling shop service for username {}: {}", username, e.getMessage(), e);
                  throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
            }
      }

}