package com.tien.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {

      @NotNull(message = "PRODUCT_NAME_IS_REQUIRED")
      @Size(max = 255, message = "PRODUCT_NAME_MUST_NOT_EXCEED")
      String name;

      @Size(max = 500, message = "DESCRIPTION_MUST_NOT_EXCEED")
      String description;

      @NotNull(message = "CATEGORY_ID_IS_REQUIRED")
      String categoryId;

}