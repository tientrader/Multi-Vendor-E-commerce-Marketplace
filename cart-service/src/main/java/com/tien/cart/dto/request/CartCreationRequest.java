package com.tien.cart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartCreationRequest {

      @NotEmpty(message = "PRODUCTS_LIST_CANNOT_BE_EMPTY")
      @Valid
      List<CartItemCreationRequest> items;

}