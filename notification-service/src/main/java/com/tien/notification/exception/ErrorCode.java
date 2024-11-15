package com.tien.notification.exception;

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

      UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
      INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
      UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
      UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
      CANNOT_SEND_EMAIL(1008, "Cannot send email", HttpStatus.BAD_REQUEST),
      EXTERNAL_SERVICE_ERROR(1009, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      DATA_INTEGRITY_VIOLATION(1010, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1011, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1012, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1013, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1014, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}