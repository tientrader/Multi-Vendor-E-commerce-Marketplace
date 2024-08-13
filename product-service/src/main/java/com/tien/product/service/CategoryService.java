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

import org.springframework.security.access.prepost.PreAuthorize;
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

      // Create a new category
      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
            ShopResponse shopResponse = shopClient.getShopByOwnerUsername(username).getResult();
            if (shopResponse == null) throw new AppException(ErrorCode.SHOP_NOT_FOUND);

            Category category = categoryMapper.toCategory(request);
            category.setShopId(shopResponse.getId());
            Category savedCategory = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(savedCategory);
      }

      // Update a category based on categoryId
      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
            ShopResponse shopResponse = shopClient.getShopByOwnerUsername(username).getResult();
            if (shopResponse == null) throw new AppException(ErrorCode.SHOP_NOT_FOUND);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            if (!category.getShopId().equals(shopResponse.getId())) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryMapper.updateCategory(category, request);
            Category updatedCategory = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(updatedCategory);
      }

      // Delete a category based on categoryId
      @PreAuthorize("hasRole('SELLER')")
      @Transactional
      public void deleteCategory(String categoryId) {
            String username = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal()).getClaim("preferred_username");
            ShopResponse shopResponse = shopClient.getShopByOwnerUsername(username).getResult();
            if (shopResponse == null) throw new AppException(ErrorCode.SHOP_NOT_FOUND);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            if (!category.getShopId().equals(shopResponse.getId())) {
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryRepository.deleteById(categoryId);
      }

      // Display all categories
      public List<CategoryResponse> getAllCategories() {
            return categoryRepository.findAll().stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
      }

      // Display category by categoryId
      public CategoryResponse getCategoryById(String categoryId) {
            return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
      }

}