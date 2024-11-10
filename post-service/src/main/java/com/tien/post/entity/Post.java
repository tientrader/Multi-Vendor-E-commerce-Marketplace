package com.tien.post.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(value = "posts")
public class Post {

      @MongoId
      String id;

      String username;
      String content;
      List<String> imageUrls;
      long likesCount;
      long commentsCount;
      Instant createdDate;
      Instant modifiedDate;

}