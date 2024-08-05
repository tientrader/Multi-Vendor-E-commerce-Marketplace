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

      @Size(min = 4, message = "USERNAME_INVALID")
      @NotBlank(message = "USERNAME_IS_REQUIRED")
      String username;

      @Email(message = "INVALID_EMAIL")
      @NotBlank(message = "EMAIL_IS_REQUIRED")
      String email;

      @NotEmpty(message = "ITEMS_CANNOT_BE_EMPTY")
      List<OrderItemCreationRequest> items;

      @Positive(message = "TOTAL_MUST_BE_POSITIVE")
      double total;

      @NotBlank(message = "STATUS_IS_REQUIRED")
      String status;

}