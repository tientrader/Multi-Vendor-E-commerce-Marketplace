package com.tien.identity.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    FIRSTNAME_NULL(1009, "First name must not be null!", HttpStatus.BAD_REQUEST),
    LASTNAME_NULL(1010, "Last name must not be null!", HttpStatus.BAD_REQUEST),
    TOKEN_NULL(1011, "Token must not be null!", HttpStatus.BAD_REQUEST),
    PASSWORD_NULL(1012, "Password must not be null!", HttpStatus.BAD_REQUEST),
    USERNAME_NULL(1013, "Username must not be null!", HttpStatus.BAD_REQUEST),
    DOB_NULL(1014, "Date of birth must not be null!", HttpStatus.BAD_REQUEST),
    CITY_NULL(1015, "City must not be null!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1016, "Role not found!", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(1017, "Permissions not found!", HttpStatus.NOT_FOUND),
    PASSWORD_EXISTED(1018, "Password existed", HttpStatus.BAD_REQUEST),
    ALREADY_HAVE_A_SHOP(1019, "ALREADY_HAVE_A_SHOP", HttpStatus.BAD_REQUEST),
    SHOP_NOT_FOUND(1020, "SHOP_NOT_FOUND", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}