package com.tien.post.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreationRequest {

      @NotNull(message = "CONTENT_IS_REQUIRED")
      @Size(min = 1, max = 500, message = "CONTENT_SIZE_CONSTRAINT")
      String content;

}