package com.tien.profile.mapper;

import com.tien.profile.dto.request.CategoryCreationRequest;
import com.tien.profile.dto.request.CategoryUpdateRequest;
import com.tien.profile.dto.response.CategoryResponse;
import com.tien.profile.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

      @Mapping(target = "id", ignore = true)
      Category toCategory(CategoryCreationRequest request);

      CategoryResponse toCategoryResponse(Category category);

      @Mapping(target = "id", ignore = true)
      void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);

}