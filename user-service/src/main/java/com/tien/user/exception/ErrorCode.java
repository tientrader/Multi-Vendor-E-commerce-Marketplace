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
    ROLE_NOT_FOUND(1011, "ROLE_NOT_FOUND", HttpStatus.NOT_FOUND),
    CLIENT_NOT_FOUND(1012, "CLIENT_NOT_FOUND", HttpStatus.NOT_FOUND),
    REALM_NOT_FOUND(1013, "REALM_NOT_FOUND", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(1014, "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTED(1015, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}