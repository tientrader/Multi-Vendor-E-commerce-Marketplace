package com.tien.profile.controller;

import com.tien.profile.dto.ApiResponse;
import com.tien.profile.dto.request.CategoryCreationRequest;
import com.tien.profile.dto.request.CategoryUpdateRequest;
import com.tien.profile.dto.response.CategoryResponse;
import com.tien.profile.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

      CategoryService categoryService;

      @PostMapping
      ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.createCategory(request))
                    .build();
      }

      @PutMapping("/{categoryId}")
      ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId, @RequestBody @Valid CategoryUpdateRequest request) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.updateCategory(categoryId, request))
                    .build();
      }

      @GetMapping("/{categoryId}")
      ApiResponse<CategoryResponse> getCategoryById(@PathVariable("categoryId") String categoryId) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.getCategoryById(categoryId))
                    .build();
      }

      @GetMapping
      ApiResponse<List<CategoryResponse>> getAllCategories() {
            return ApiResponse.<List<CategoryResponse>>builder()
                    .result(categoryService.getAllCategories())
                    .build();
      }

      @DeleteMapping("/{categoryId}")
      ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
            categoryService.deleteCategory(categoryId);
            return ApiResponse.<String>builder()
                    .result("Category has been deleted")
                    .build();
      }

}