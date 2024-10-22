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
      USERNAME_IS_REQUIRED(1011, "USERNAME_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      USERNAME_INVALID(1012, "USERNAME_INVALID", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(1013, "EMAIL_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(1014, "INVALID_EMAIL", HttpStatus.BAD_REQUEST),
      PRODUCTS_LIST_CANNOT_BE_EMPTY(1015, "PRODUCTS_LIST_CANNOT_BE_EMPTY", HttpStatus.BAD_REQUEST),
      ITEMS_CANNOT_BE_EMPTY(1016, "ITEMS_CANNOT_BE_EMPTY", HttpStatus.BAD_REQUEST),
      TOTAL_MUST_BE_POSITIVE(1017, "TOTAL_MUST_BE_POSITIVE", HttpStatus.BAD_REQUEST),
      STATUS_IS_REQUIRED(1018, "STATUS_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      PRODUCT_ID_IS_REQUIRED(1019, "PRODUCT_ID_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      QUANTITY_IS_REQUIRED(1020, "QUANTITY_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      QUANTITY_MUST_BE_POSITIVE(1021, "QUANTITY_MUST_BE_POSITIVE", HttpStatus.BAD_REQUEST),
      PRODUCT_SERVICE_UNAVAILABLE(1022, "PRODUCT_SERVICE_UNAVAILABLE", HttpStatus.BAD_REQUEST),
      ORDER_SERVICE_UNAVAILABLE(1023, "ORDER_SERVICE_UNAVAILABLE", HttpStatus.BAD_REQUEST),
      OUT_OF_STOCK(1024, "OUT_OF_STOCK", HttpStatus.BAD_REQUEST),
      CANNOT_ADD_OWN_PRODUCT(1025, "CANNOT_ADD_OWN_PRODUCT", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}