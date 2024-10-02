package com.tien.product.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "products")
public class Product {

      @Id
      String id;
      String shopId;
      String name;
      String description;
      double price;
      int stock;
      int soldQuantity;

      @Field
      LocalDateTime createdAt;

      @DBRef
      Category category;

}