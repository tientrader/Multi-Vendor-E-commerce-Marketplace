package com.tien.review.exception;

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
      EXTERNAL_SERVICE_ERROR(1004, "EXTERNAL_SERVICE_ERROR", HttpStatus.FORBIDDEN),
      DATA_INTEGRITY_VIOLATION(1005, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1006, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1007, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1008, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1009, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      REVIEW_NOT_FOUND(1010, "REVIEW_NOT_FOUND", HttpStatus.NOT_FOUND),
      PRODUCT_NOT_FOUND(1011, "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND),
      USER_NOT_PURCHASED_PRODUCT(1012, "USER_NOT_PURCHASED_PRODUCT", HttpStatus.FORBIDDEN),
      ALREADY_REVIEWED(1013, "ALREADY_REVIEWED", HttpStatus.FORBIDDEN),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}