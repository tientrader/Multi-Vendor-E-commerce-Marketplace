package com.tien.promotion.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

      String cartId;
      String username;
      String email;
      List<CartItemResponse> items;
      double total;

}