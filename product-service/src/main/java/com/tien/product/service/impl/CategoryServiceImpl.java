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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

      CategoryRepository categoryRepository;
      CategoryMapper categoryMapper;
      ShopClient shopClient;

      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            String username = getCurrentUsername();
            log.info("Creating category for shop owner: {}", username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(createCategory) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Category category = categoryMapper.toCategory(request);
            category.setShopId(shopResponse.getId());
            Category savedCategory = categoryRepository.save(category);

            log.info("Category created with ID: {}", savedCategory.getId());
            return categoryMapper.toCategoryResponse(savedCategory);
      }

      public List<CategoryResponse> getAllCategories() {
            log.info("Fetching all categories");
            return categoryRepository.findAll().stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
      }

      public CategoryResponse getCategoryById(String categoryId) {
            log.info("Fetching category with ID: {}", categoryId);
            return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                          log.error("(getCategoryById) Category not found with ID: {}", categoryId);
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    }));
      }

      public List<CategoryResponse> getCategoriesByShopId(String shopId) {
            log.info("Fetching categories for shop ID: {}", shopId);
            return categoryMapper.toCategoryResponses(categoryRepository.findByShopId(shopId));
      }

      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            String username = getCurrentUsername();
            log.info("Updating category with ID: {} by shop owner: {}", categoryId, username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(updateCategory) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                          log.error("(updateCategory) Category not found with ID: {}", categoryId);
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });

            if (!category.getShopId().equals(shopResponse.getId())) {
                  log.error("Unauthorized update attempt by owner: {}", username);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryMapper.updateCategory(category, request);
            Category updatedCategory = categoryRepository.save(category);

            log.info("Category updated with ID: {}", updatedCategory.getId());
            return categoryMapper.toCategoryResponse(updatedCategory);
      }

      @Transactional
      public void deleteCategory(String categoryId) {
            String username = getCurrentUsername();
            log.info("Deleting category with ID: {} by shop owner: {}", categoryId, username);

            ShopResponse shopResponse = getShopByOwnerUsername(username)
                    .orElseThrow(() -> {
                          log.error("(deleteCategory) Shop not found for owner: {}", username);
                          return new AppException(ErrorCode.SHOP_NOT_FOUND);
                    });

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> {
                          log.error("(deleteCategory) Category not found with ID: {}", categoryId);
                          return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                    });

            if (!category.getShopId().equals(shopResponse.getId())) {
                  log.error("Unauthorized delete attempt by owner: {}", username);
                  throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            categoryRepository.deleteById(categoryId);
            log.info("Category deleted with ID: {}", categoryId);
      }

      private String getCurrentUsername() {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return jwt.getClaim("preferred_username");
      }

      private Optional<ShopResponse> getShopByOwnerUsername(String username) {
            return Optional.ofNullable(shopClient.getShopByOwnerUsername(username).getResult());
      }

}