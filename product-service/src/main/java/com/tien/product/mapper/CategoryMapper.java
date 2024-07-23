package com.tien.product.mapper;

import com.tien.product.dto.response.CategoryResponse;
import com.tien.product.entity.Category;
import com.tien.product.dto.request.CategoryCreationRequest;
import com.tien.product.dto.request.CategoryUpdateRequest;
import com.tien.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

      @Mapping(target = "shopId", ignore = true)
      @Mapping(target = "products", ignore = true)
      @Mapping(target = "id", ignore = true)
      Category toCategory(CategoryCreationRequest request);

      @Mapping(target = "productIds", source = "products", qualifiedByName = "mapProductIds")
      CategoryResponse toCategoryResponse(Category category);

      @Mapping(target = "shopId", ignore = true)
      @Mapping(target = "products", ignore = true)
      @Mapping(target = "id", ignore = true)
      void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);

      @Named("mapProductIds")
      static Set<String> mapProductIds(Set<Product> products) {
            if (products != null) {
                  return products.stream().map(Product::getId).collect(Collectors.toSet());
            }
            return Collections.emptySet();
      }

}