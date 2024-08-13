package com.tien.shop.exception;

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
    INVALID_USERNAME(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_EXISTED(1008, "Email existed, please choose another one", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1009, "Username existed, please choose another one", HttpStatus.BAD_REQUEST),
    USERNAME_IS_MISSING(1010, "Please enter username", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1011, "Role not found", HttpStatus.NOT_FOUND),
    CLIENT_NOT_FOUND(1011, "Client not found", HttpStatus.NOT_FOUND),
    REALM_NOT_FOUND(1011, "Realm not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(1011, "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND),
    SHOP_NOT_FOUND(1012, "SHOP_NOT_FOUND", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTED(1013, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    ALREADY_HAVE_A_SHOP(1014, "ALREADY_HAVE_A_SHOP", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}