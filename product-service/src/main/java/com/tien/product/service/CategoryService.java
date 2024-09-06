package com.tien.product.service;

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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

      CategoryRepository categoryRepository;
      CategoryMapper categoryMapper;
      ShopClient shopClient;

      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);
            if (shopResponse == null) throw new AppException(ErrorCode.SHOP_NOT_FOUND);

            Category category = categoryMapper.toCategory(request);
            category.setShopId(shopResponse.getId());
            Category savedCategory = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(savedCategory);
      }

      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            if (shopResponse == null) {
                  throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            if (!category.getShopId().equals(shopResponse.getId())) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryMapper.updateCategory(category, request);
            Category updatedCategory = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(updatedCategory);
      }

      public List<CategoryResponse> getAllCategories() {
            return categoryRepository.findAll().stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
      }

      public CategoryResponse getCategoryById(String categoryId) {
            return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
      }

      public List<CategoryResponse> getCategoriesByShopId(String shopId) {
            List<Category> categories = categoryRepository.findByShopId(shopId);
            return categoryMapper.toCategoryResponses(categories);
      }

      @Transactional
      public void deleteCategory(String categoryId) {
            String username = getCurrentUsername();
            ShopResponse shopResponse = getShopByOwnerUsername(username);

            if (shopResponse == null) {
                  throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            if (!category.getShopId().equals(shopResponse.getId())) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryRepository.deleteById(categoryId);
      }

      private String getCurrentUsername() {
            return ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
      }

      private ShopResponse getShopByOwnerUsername(String username) {
            return shopClient.getShopByOwnerUsername(username).getResult();
      }

}