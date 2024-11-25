package com.tien.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreationRequest {

      @NotNull(message = "PRODUCT_ID_IS_REQUIRED")
      String productId;

      @NotNull(message = "VARIANT_ID_IS_REQUIRED")
      String variantId;

      @NotNull(message = "RATING_IS_REQUIRED")
      @Min(value = 1, message = "RATING_TOO_LOW")
      @Max(value = 5, message = "RATING_TOO_HIGH")
      int rating;

      @Size(max = 500, message = "CONTENT_TOO_LONG")
      String content;

}