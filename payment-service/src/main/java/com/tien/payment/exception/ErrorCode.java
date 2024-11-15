package com.tien.payment.exception;

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
      SUBSCRIPTION_CANCEL_FAILED(1008, "SUBSCRIPTION_CANCEL_FAILED", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_SESSION_CREATION_FAILED(1009, "SUBSCRIPTION_SESSION_CREATION_FAILED", HttpStatus.BAD_REQUEST),
      PAYMENT_SESSION_CREATION_FAILED(1010, "PAYMENT_SESSION_CREATION_FAILED", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CREATION_FAILED(1011, "SUBSCRIPTION_CREATION_FAILED", HttpStatus.BAD_REQUEST),
      PAYMENT_FAILED(1012, "PAYMENT_FAILED", HttpStatus.BAD_REQUEST),
      EXTERNAL_SERVICE_ERROR(1013, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      DATA_INTEGRITY_VIOLATION(1014, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1015, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1016, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1017, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1018, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}