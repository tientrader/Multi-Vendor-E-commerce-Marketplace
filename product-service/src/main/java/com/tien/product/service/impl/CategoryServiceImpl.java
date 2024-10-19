package com.tien.product.service.impl;

import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.dto.response.ShopResponse;
import com.tien.product.entity.Category;
import com.tien.product.exception.ErrorCode;
import com.tien.product.httpclient.ShopClient;
import com.tien.product.mapper.CategoryMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import com.tien.product.exception.AppException;
import com.tien.product.service.CategoryService;
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
            Category category = validateCategoryOwnership(categoryId, shopResponse.getId());

            categoryMapper.updateCategory(category, request);
            Category updatedCategory = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(updatedCategory);
      }

      @Override
      @Transactional
      public void deleteCategory(String categoryId) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);
            validateCategoryOwnership(categoryId, shopResponse.getId());

            categoryRepository.deleteById(categoryId);
      }

      @Override
      public List<CategoryResponse> getAllCategories() {
            return categoryMapper.toCategoryResponses(categoryRepository.findAll());
      }

      @Override
      public CategoryResponse getCategoryById(String categoryId) {
            return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
      }

      @Override
      public List<CategoryResponse> getCategoriesByShopId(String shopId) {
            return categoryMapper.toCategoryResponses(categoryRepository.findByShopId(shopId));
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            return Optional.ofNullable(shopClient.getShopByOwnerUsername(username).getResult())
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
      }

      private Category validateCategoryOwnership(String categoryId, String shopId) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            if (!category.getShopId().equals(shopId)) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return category;
      }

}