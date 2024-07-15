package com.tien.cart.dto.request;

import com.tien.cart.entity.ProductInCart;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartCreationRequest {

      List<ProductInCart> products;

}