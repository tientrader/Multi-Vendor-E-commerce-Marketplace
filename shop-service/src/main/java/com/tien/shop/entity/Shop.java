package com.tien.shop.entity;

import jakarta.validation.constraints.NotNull;
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

      @NotNull
      @Indexed(unique = true)
      String name;

      @NotNull
      @Indexed
      String email;

      @NotNull
      String ownerUsername;

}