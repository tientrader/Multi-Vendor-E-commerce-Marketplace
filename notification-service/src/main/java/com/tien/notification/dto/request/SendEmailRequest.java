package com.tien.notification.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {

      @NotNull(message = "RECIPIENT_IS_REQUIRED")
      Recipient to;

      @NotNull(message = "SUBJECT_IS_REQUIRED")
      @Size(min = 1, max = 255, message = "SUBJECT_SIZE_CONSTRAINT")
      String subject;

      @NotNull(message = "CONTENT_IS_REQUIRED")
      String htmlContent;

}