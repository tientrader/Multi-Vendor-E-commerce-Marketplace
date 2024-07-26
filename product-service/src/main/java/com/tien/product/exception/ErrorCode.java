package com.tien.product.exception;

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
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You do not have permission", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND(1004, "Product not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1005, "Category not found", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(1006, "Out of stock", HttpStatus.BAD_REQUEST),
    SHOP_NOT_FOUND(1006, "SHOP_NOT_FOUND", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE(1007, "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}