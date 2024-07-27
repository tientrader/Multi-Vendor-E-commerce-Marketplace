package com.tien.cart.exception;

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

    UNCATEGORIZED_EXCEPTION(9999, "UNCATEGORIZED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "INVALID_KEY", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "UNAUTHORIZED", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND(1004, "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1005, "CART_NOT_FOUND", HttpStatus.NOT_FOUND),
    REDIS_SAVE_FAILED(1006, "REDIS_SAVE_FAILED", HttpStatus.NOT_FOUND),
    TOTAL_CALCULATION_FAILED(1007, "TOTAL_CALCULATION_FAILED", HttpStatus.NOT_FOUND),
    PRODUCT_FETCH_FAILED(1008, "PRODUCT_FETCH_FAILED", HttpStatus.NOT_FOUND),
    ORDER_CREATION_FAILED(1009, "ORDER_CREATION_FAILED", HttpStatus.NOT_FOUND),
    SERVICE_UNAVAILABLE(1010, "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}