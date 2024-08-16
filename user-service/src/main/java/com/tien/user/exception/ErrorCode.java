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
    DOB_NULL(1016, "DOB_NULL", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1017, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    FIRSTNAME_NULL(1018, "FIRSTNAME_NULL", HttpStatus.BAD_REQUEST),
    LASTNAME_NULL(1019, "LASTNAME_NULL", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1020, "INVALID_EMAIL", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1021, "EMAIL_IS_REQUIRED", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}