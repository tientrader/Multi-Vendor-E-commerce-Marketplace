package com.tien.review.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "reviews")
public class Review {

      @Id
      String id;

      String username;
      String productId;
      String variantId;
      int rating;
      String content;
      List<String> imageUrls;
      String shopResponse;

      @CreatedDate
      LocalDateTime createdAt;

      @LastModifiedDate
      LocalDateTime updatedAt;

}