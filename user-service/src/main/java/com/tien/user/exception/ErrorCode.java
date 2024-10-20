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
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
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
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}