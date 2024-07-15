package com.tien.cart.dto.response;

import com.tien.cart.entity.ProductInCart;
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
      String userId;
      List<ProductInCart> products;

}