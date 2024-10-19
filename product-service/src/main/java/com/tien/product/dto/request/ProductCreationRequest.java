package com.tien.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {

      @NotBlank(message = "PRODUCT_NAME_IS_REQUIRED")
      String name;

      String description;

      @NotBlank(message = "CATEGORY_ID_IS_REQUIRED")
      String categoryId;

      @Valid
      List<ProductVariantRequest> variants;

}