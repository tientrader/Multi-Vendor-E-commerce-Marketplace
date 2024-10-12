package com.tien.product.mapper;

import com.tien.product.dto.request.ProductVariantRequest;
import com.tien.product.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

      @Mapping(target = "variantId", expression = "java(java.util.UUID.randomUUID().toString())")
      ProductVariant toProductVariant(ProductVariantRequest request);

}