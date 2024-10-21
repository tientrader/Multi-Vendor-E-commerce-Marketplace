package com.tien.post.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

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
      Instant createdDate;
      Instant modifiedDate;
      long likesCount;
      long commentsCount;

}