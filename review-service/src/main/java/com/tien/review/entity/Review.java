package com.tien.review.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
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

      @NotNull
      @Indexed
      String username;

      @NotNull
      @Indexed
      String productId;

      @NotNull
      String variantId;

      @NotNull
      @Min(1)
      @Max(5)
      int rating;

      List<String> imageUrls;

      @Size(max = 500)
      String content;

      @Size(max = 500)
      String shopResponse;

      @CreatedDate
      LocalDateTime createdAt;

      @LastModifiedDate
      LocalDateTime updatedAt;

}