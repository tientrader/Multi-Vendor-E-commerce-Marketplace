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

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND(1009, "Product not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1009, "Cart not found", HttpStatus.NOT_FOUND),
    REDIS_SAVE_FAILED(1009, "REDIS_SAVE_FAILED", HttpStatus.NOT_FOUND),
    TOTAL_CALCULATION_FAILED(1009, "TOTAL_CALCULATION_FAILED", HttpStatus.NOT_FOUND),
    PRODUCT_FETCH_FAILED(1009, "PRODUCT_FETCH_FAILED", HttpStatus.NOT_FOUND),
    ORDER_CREATION_FAILED(1009, "ORDER_CREATION_FAILED", HttpStatus.NOT_FOUND),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}