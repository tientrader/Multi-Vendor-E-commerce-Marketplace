package com.tien.product.controller;

import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.service.CategoryService;
import com.tien.product.dto.ApiResponse;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
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
      public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreationRequest request) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.createCategory(request))
                    .build();
      }

      @PutMapping("/{categoryId}")
      public ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId,
                                                          @RequestBody @Valid CategoryUpdateRequest request) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.updateCategory(categoryId, request))
                    .build();
      }

      @DeleteMapping("/{categoryId}")
      public ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
            categoryService.deleteCategory(categoryId);
            return ApiResponse.<String>builder()
                    .result("Category has been deleted")
                    .build();
      }

      @GetMapping("/{categoryId}")
      public ApiResponse<CategoryResponse> getCategoryById(@PathVariable("categoryId") String categoryId) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.getCategoryById(categoryId))
                    .build();
      }

      @GetMapping
      public ApiResponse<List<CategoryResponse>> getAllCategories() {
            return ApiResponse.<List<CategoryResponse>>builder()
                    .result(categoryService.getAllCategories())
                    .build();
      }

      @GetMapping("/shop/{shopId}")
      public ApiResponse<List<CategoryResponse>> getCategoriesByShopId(@PathVariable String shopId) {
            return ApiResponse.<List<CategoryResponse>>builder()
                    .result(categoryService.getCategoriesByShopId(shopId))
                    .build();
      }

}