package com.tien.product.mapper;

import com.tien.product.dto.request.ProductUpdateRequest;
import com.tien.product.dto.request.ProductCreationRequest;
import com.tien.product.dto.request.ProductVariantRequest;
import com.tien.product.dto.response.ProductResponse;
import com.tien.product.entity.Product;
import com.tien.product.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

      Product toProduct(ProductCreationRequest request);

      @Mapping(target = "categoryId", source = "category.id")
      ProductResponse toProductResponse(Product product);

      void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

      List<ProductResponse> toProductResponses(List<Product> products);

      List<ProductVariant> toVariants(List<ProductVariantRequest> variantRequests);

}