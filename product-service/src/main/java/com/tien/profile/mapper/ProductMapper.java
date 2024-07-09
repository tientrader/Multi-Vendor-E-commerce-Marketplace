package com.tien.profile.mapper;

import com.tien.profile.dto.request.ProductCreationRequest;
import com.tien.profile.dto.request.ProductUpdateRequest;
import com.tien.profile.dto.response.ProductResponse;
import com.tien.profile.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

      @Mapping(target = "category", ignore = true)
      @Mapping(target = "id", ignore = true)
      Product toProduct(ProductCreationRequest request);

      @Mapping(source = "category.name", target = "categoryName")
      ProductResponse toProductResponse(Product product);

      @Mapping(target = "category", ignore = true)
      @Mapping(target = "id", ignore = true)
      void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

}