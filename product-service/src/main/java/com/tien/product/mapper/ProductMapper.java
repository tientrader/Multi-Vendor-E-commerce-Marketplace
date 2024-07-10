package com.tien.product.mapper;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
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