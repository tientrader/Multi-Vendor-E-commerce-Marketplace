package com.tien.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Document(collection = "products")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

      @Id
      String id;
      String shopId;
      String name;
      String description;
      int soldQuantity;
      List<ProductVariant> variants;
      String categoryId;
      List<String> imageUrls;

      @CreatedDate
      LocalDateTime createdAt;

      @LastModifiedDate
      LocalDateTime updatedAt;

}