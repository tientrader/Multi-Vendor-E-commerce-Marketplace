package com.tien.cart.dto.request;

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

      @Size(min = 4, message = "USERNAME_INVALID")
      @NotBlank(message = "USERNAME_IS_REQUIRED")
      String username;

      @Email(message = "INVALID_EMAIL")
      @NotBlank(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotEmpty(message = "PRODUCTS_LIST_CANNOT_BE_EMPTY")
      List<ProductInCartCreationRequest> productInCarts;

}