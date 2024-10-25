package com.tien.product.mapper;

import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.entity.Category;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

      Category toCategory(CategoryCreationRequest request);

      CategoryResponse toCategoryResponse(Category category);

      void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);

      List<CategoryResponse> toCategoryResponses(List<Category> categories);

}