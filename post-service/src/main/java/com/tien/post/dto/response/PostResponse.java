package com.tien.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {

      String id;
      String content;
      List<String> imageUrls;
      String username;
      String created;
      long likesCount;
      long commentsCount;
      Instant createdDate;
      Instant modifiedDate;

}