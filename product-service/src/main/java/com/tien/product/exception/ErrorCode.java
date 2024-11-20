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
      SHOP_NOT_FOUND(1007, "SHOP_NOT_FOUND", HttpStatus.BAD_REQUEST),
      SHOP_SERVICE_UNAVAILABLE(1008, "SHOP_SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
      PRODUCT_NAME_IS_REQUIRED(1009, "PRODUCT_NAME_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      PRICE_MUST_BE_POSITIVE(1010, "PRICE_MUST_BE_POSITIVE", HttpStatus.BAD_REQUEST),
      STOCK_MUST_BE_POSITIVE(1011, "STOCK_MUST_BE_POSITIVE", HttpStatus.BAD_REQUEST),
      CATEGORY_ID_IS_REQUIRED(1012, "CATEGORY_ID_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      CATEGORY_NAME_IS_REQUIRED(1013, "CATEGORY_NAME_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      VARIANT_NOT_FOUND(1014, "VARIANT_NOT_FOUND", HttpStatus.BAD_REQUEST),
      STOCK_IS_REQUIRED(1015, "STOCK_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      PRICE_IS_REQUIRED(1016, "PRICE_IS_REQUIRED", HttpStatus.BAD_REQUEST),
      EXTERNAL_SERVICE_ERROR(1017, "EXTERNAL_SERVICE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
      IMAGE_REQUIRED(1018, "IMAGE_REQUIRED", HttpStatus.BAD_REQUEST),
      DATA_INTEGRITY_VIOLATION(1019, "DATA_INTEGRITY_VIOLATION", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1020, "DUPLICATE_ENTRY", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1021, "FOREIGN_KEY_VIOLATION", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1022, "FILE_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1023, "MISSING_REQUIRED_PARAMETER", HttpStatus.BAD_REQUEST),
      FILE_UPLOAD_FAILED(1024, "FILE_UPLOAD_FAILED", HttpStatus.SERVICE_UNAVAILABLE),
      SERVICE_UNAVAILABLE(1025, "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}