package com.tien.shop.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("shop")
public class Shop {

      @Id
      String id;

      @Indexed(unique = true)
      String name;

      @Indexed
      String email;

      String ownerUsername;

}