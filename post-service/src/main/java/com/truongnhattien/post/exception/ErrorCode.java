package com.truongnhattien.post.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "UNCATEGORIZED_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "INVALID_KEY", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "UNAUTHORIZED", HttpStatus.FORBIDDEN),
    PROFILE_NOT_FOUND(1005, "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND(1006, "POST_NOT_FOUND", HttpStatus.NOT_FOUND),
    LIKE_NOT_FOUND(1007, "LIKE_NOT_FOUND", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(1008, "COMMENT_NOT_FOUND", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}