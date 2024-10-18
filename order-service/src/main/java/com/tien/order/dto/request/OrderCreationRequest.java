package com.tien.order.dto.request;

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

      String username;

      @Email(message = "INVALID_EMAIL")
      @NotBlank(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotEmpty(message = "ITEMS_CANNOT_BE_EMPTY")
      List<OrderItemCreationRequest> items;

      double total;
      String status;
      String paymentMethod;
      String paymentToken;

}