package com.tien.cart.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("Cart")
public class Cart {

      @Id
      String id;
      String userId;
      List<ProductInCart> productInCarts = new ArrayList<>();
      double total;

}