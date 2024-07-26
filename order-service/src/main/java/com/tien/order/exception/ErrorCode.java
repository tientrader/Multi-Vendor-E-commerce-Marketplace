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

    UNCATEGORIZED_EXCEPTION(9999, "UNCATEGORIZED_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "INVALID_KEY", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "USER_NOT_EXISTED", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "UNAUTHORIZED", HttpStatus.FORBIDDEN),
    PRODUCT_NOT_FOUND(1008, "PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND),
    PROFILE_NOT_FOUND(1009, "PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND),
    STOCK_UPDATE_FAIL(1010, "STOCK_UPDATE_FAIL", HttpStatus.BAD_REQUEST),
    PRODUCT_FETCH_FAIL(1011, "PRODUCT_FETCH_FAIL", HttpStatus.BAD_REQUEST),
    NULL_PRICE(1012, "NULL_PRICE", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(1013, "INSUFFICIENT_STOCK", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1014, "ORDER_NOT_FOUND", HttpStatus.BAD_REQUEST),
    ORDER_IS_NOT_YOURS(1015, "ORDER_IS_NOT_YOURS", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE(1016, "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}