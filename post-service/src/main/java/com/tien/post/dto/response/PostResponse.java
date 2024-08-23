package com.tien.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {

    String id;
    String content;
    String userId;
    String created;
    Instant createdDate;
    Instant modifiedDate;
    long likesCount;
    long commentsCount;

}