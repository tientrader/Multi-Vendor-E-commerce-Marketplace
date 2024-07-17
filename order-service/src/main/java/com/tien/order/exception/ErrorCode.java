package com.tien.order.exception;

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
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND(1008, "Product not found", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(1009, "Profile not found", HttpStatus.NOT_FOUND),
    STOCK_UPDATE_FAIL(1010, "STOCK_UPDATE_FAIL", HttpStatus.NOT_FOUND),
    PRODUCT_FETCH_FAIL(1011, "PRODUCT_FETCH_FAIL", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}