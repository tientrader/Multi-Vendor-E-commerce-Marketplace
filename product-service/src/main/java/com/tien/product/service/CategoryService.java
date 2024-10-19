package com.tien.product.service;

import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import com.tien.product.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

      CategoryResponse createCategory(CategoryCreationRequest request);
      CategoryResponse updateCategory(String categoryId, CategoryUpdateRequest request);
      void deleteCategory(String categoryId);
      List<CategoryResponse> getAllCategories();
      CategoryResponse getCategoryById(String categoryId);
      List<CategoryResponse> getCategoriesByShopId(String shopId);

}