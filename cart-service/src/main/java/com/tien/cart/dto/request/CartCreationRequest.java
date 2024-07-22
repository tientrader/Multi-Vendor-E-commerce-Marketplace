package com.tien.cart.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartCreationRequest {

      String userId;
      String email;
      List<ProductInCartCreationRequest> productInCarts;

}