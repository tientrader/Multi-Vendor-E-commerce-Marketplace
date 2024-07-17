package com.tien.product.service;

import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.entity.Category;
import com.tien.product.exception.ErrorCode;
import com.tien.product.mapper.CategoryMapper;
import com.tien.product.repository.CategoryRepository;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import com.tien.product.exception.AppException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
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

      // Create a new category
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            Category category = categoryMapper.toCategory(request);
            return categoryMapper.toCategoryResponse(categoryRepository.save(category));
      }

      // Update a category based on categoryId
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryMapper.updateCategory(category, request);
            return categoryMapper.toCategoryResponse(categoryRepository.save(category));
      }

      // Delete a category based on categoryId
      @PreAuthorize("hasRole('ADMIN')")
      @Transactional
      public void deleteCategory(String categoryId) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
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