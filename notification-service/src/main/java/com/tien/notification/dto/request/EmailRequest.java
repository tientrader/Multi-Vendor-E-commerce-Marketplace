package com.tien.notification.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {

      @NotNull(message = "SENDER_IS_REQUIRED")
      Sender sender;

      @NotNull(message = "RECIPIENTS_ARE_REQUIRED")
      @Size(min = 1, message = "AT_LEAST_ONE_RECIPIENT_IS_REQUIRED")
      List<Recipient> to;

      @NotNull(message = "SUBJECT_IS_REQUIRED")
      @Size(min = 1, max = 255, message = "SUBJECT_SIZE_CONSTRAINT")
      String subject;

      @NotNull(message = "CONTENT_IS_REQUIRED")
      String htmlContent;

}