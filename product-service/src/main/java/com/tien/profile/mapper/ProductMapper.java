package com.tien.profile.mapper;

import com.tien.profile.dto.request.ProductCreationRequest;
import com.tien.profile.dto.request.ProductUpdateRequest;
import com.tien.profile.dto.response.ProductResponse;
import com.tien.profile.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

      @Mapping(target = "id", ignore = true)
      @Mapping(target = "category", ignore = true)
      Product toProduct(ProductCreationRequest request);

      @Mapping(target = "categoryId", source = "category.id")
      ProductResponse toProductResponse(Product product);

      @Mapping(target = "id", ignore = true)
      @Mapping(target = "category", ignore = true)
      void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

}