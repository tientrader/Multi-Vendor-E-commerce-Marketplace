package com.tien.user.exception;

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
      INVALID_USERNAME(1003, "Username must be at least 4 characters", HttpStatus.BAD_REQUEST),
      INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
      UNAUTHENTICATED(1006, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
      UNAUTHORIZED(1007, "UNAUTHORIZED", HttpStatus.FORBIDDEN),
      EMAIL_EXISTED(1008, "EMAIL_EXISTED", HttpStatus.BAD_REQUEST),
      USER_EXISTED(1009, "USER_EXISTED", HttpStatus.BAD_REQUEST),
      USERNAME_IS_MISSING(1010, "USERNAME_IS_MISSING", HttpStatus.BAD_REQUEST),
      PROFILE_NOT_FOUND(1011, "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND),
      USER_NOT_EXISTED(1012, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
      DOB_NULL(1013, "DOB_NULL", HttpStatus.BAD_REQUEST),
      INVALID_DOB(1014, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
      FIRSTNAME_NULL(1015, "FIRSTNAME_NULL", HttpStatus.BAD_REQUEST),
      LASTNAME_NULL(1016, "LASTNAME_NULL", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(1017, "INVALID_EMAIL", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(1018, "EMAIL_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      LOGIN_FAILED(1019, "LOGIN_FAILED", HttpStatus.BAD_REQUEST),
      INVALID_USERNAME_OR_PASSWORD(1020, "INVALID_USERNAME_OR_PASSWORD", HttpStatus.BAD_REQUEST),
      INVALID_PRICE_ID(1021, "INVALID_PRICE_ID", HttpStatus.BAD_REQUEST),
      USER_NOT_VIP(1022, "USER_NOT_VIP", HttpStatus.BAD_REQUEST),
      PHONE_NUMBER_NULL(1023, "PHONE_NUMBER_NULL", HttpStatus.BAD_REQUEST),
      INVALID_PHONE_NUMBER(1024, "INVALID_PHONE_NUMBER", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CANCELLATION_FAILED(1025, "SUBSCRIPTION_CANCELLATION_FAILED", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CREATION_FAILED(1026, "SUBSCRIPTION_CREATION_FAILED", HttpStatus.BAD_REQUEST),
      EXTERNAL_SERVICE_ERROR(1027, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      INVALID_PACKAGE_TYPE(1028, "INVALID_PACKAGE_TYPE", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_SESSION_CREATION_FAILED(1029, "SUBSCRIPTION_SESSION_CREATION_FAILED", HttpStatus.BAD_REQUEST),
      INVALID_STRIPE_SIGNATURE(1030, "INVALID_STRIPE_SIGNATURE", HttpStatus.BAD_REQUEST),
      DATA_INTEGRITY_VIOLATION(1031, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1032, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1033, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1034, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1035, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      PHONE_NUMBER_EXISTED(1036, "PHONE_NUMBER_EXISTED", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}