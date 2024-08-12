package com.truongnhattien.post.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    String id;
    String postId;
    String userId;
    String content;
    Instant createdDate;
    Instant modifiedDate;

}