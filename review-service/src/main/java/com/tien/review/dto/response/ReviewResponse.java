package com.tien.review.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {

      String id;
      String username;
      String productId;
      String variantId;
      int rating;
      String content;
      List<String> imageUrls;
      LocalDateTime createdAt;
      LocalDateTime updatedAt;

}