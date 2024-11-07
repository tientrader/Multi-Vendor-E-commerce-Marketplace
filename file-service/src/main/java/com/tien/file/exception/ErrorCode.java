package com.tien.file.exception;

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
      UNAUTHENTICATED(1002, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
      UNAUTHORIZED(1003, "UNAUTHORIZED", HttpStatus.FORBIDDEN),
      EXTERNAL_SERVICE_ERROR(1004, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      FILE_NOT_FOUND(1005, "FILE_NOT_FOUND", HttpStatus.NOT_FOUND),
      DELETE_FAILED(1006, "DELETE_FAILED", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1007, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}