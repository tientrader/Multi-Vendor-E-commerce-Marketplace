package com.tien.profile.service;

import com.tien.profile.dto.request.CategoryCreationRequest;
import com.tien.profile.dto.request.CategoryUpdateRequest;
import com.tien.profile.dto.response.CategoryResponse;
import com.tien.profile.entity.Category;
import com.tien.profile.exception.AppException;
import com.tien.profile.exception.ErrorCode;
import com.tien.profile.mapper.CategoryMapper;
import com.tien.profile.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {

      CategoryRepository categoryRepository;
      CategoryMapper categoryMapper;

      @PreAuthorize("hasRole('ADMIN')")
      @CachePut(value = "categories", key = "#result.id")
      @CacheEvict(value = "allCategories", allEntries = true)
      @Transactional
      public CategoryResponse createCategory(CategoryCreationRequest request) {
            Category category = categoryMapper.toCategory(request);
            return categoryMapper.toCategoryResponse(categoryRepository.save(category));
      }

      @PreAuthorize("hasRole('ADMIN')")
      @CachePut(value = "categories", key = "#categoryId")
      @CacheEvict(value = "allCategories", allEntries = true)
      @Transactional
      public CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryMapper.updateCategory(category, request);
            return categoryMapper.toCategoryResponse(categoryRepository.save(category));
      }

      @Cacheable(value = "allCategories")
      public List<CategoryResponse> getAllCategories() {
            return categoryRepository.findAll().stream()
                    .map(categoryMapper::toCategoryResponse)
                    .collect(Collectors.toList());
      }

//      @Cacheable(value = "categories", key = "#categoryId")
      public CategoryResponse getCategoryById(String categoryId) {
            return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)));
      }

      @PreAuthorize("hasRole('ADMIN')")
      @CacheEvict(value = "allCategories", allEntries = true)
      @Transactional
      public void deleteCategory(String categoryId) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryRepository.deleteById(categoryId);
      }

}