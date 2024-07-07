package com.tien.profile.mapper;

import com.tien.profile.dto.request.ProductCreationRequest;
import com.tien.profile.dto.request.ProductUpdateRequest;
import com.tien.profile.dto.response.ProductResponse;
import com.tien.profile.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

      Product toProduct(ProductCreationRequest request);

      ProductResponse toProductResponse(Product product);

      void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

}