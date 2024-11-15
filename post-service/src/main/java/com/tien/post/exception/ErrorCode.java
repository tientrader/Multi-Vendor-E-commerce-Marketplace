package com.tien.post.exception;

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
      USER_NOT_VIP(1009, "USER_NOT_VIP", HttpStatus.NOT_FOUND),
      EXTERNAL_SERVICE_ERROR(1010, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      DATA_INTEGRITY_VIOLATION(1011, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1012, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1013, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1014, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1015, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}