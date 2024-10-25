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
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}