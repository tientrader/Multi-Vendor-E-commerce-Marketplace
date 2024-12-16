package com.tien.order.dto.request;

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
public class OrderCreationRequest {

      @NotEmpty(message = "ITEMS_CANNOT_BE_EMPTY")
      @Valid
      List<OrderItemCreationRequest> items;

      double total;

      @NotNull(message = "PAYMENT_METHOD_IS_REQUIRED")
      @Pattern(
              regexp = "^(CARD|COD)$",
              message = "PAYMENT_METHOD_MUST_BE_ONE_OF_CARD_CODE"
      )
      String paymentMethod;

      @NotNull(message = "PAYMENT_TOKEN_IS_REQUIRED")
      String paymentToken;

}