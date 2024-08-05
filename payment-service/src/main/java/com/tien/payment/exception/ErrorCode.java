package com.tien.payment.exception;

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
    PROFILE_NOT_FOUND(1009, "Profile not found", HttpStatus.NOT_FOUND),
    PAYPAL_AMOUNT_NULL(1010, "PAYPAL_AMOUNT_NULL", HttpStatus.BAD_REQUEST),
    PAYPAL_CURRENCY_BLANK(1011, "PAYPAL_CURRENCY_BLANK", HttpStatus.BAD_REQUEST),
    PAYPAL_PAYMENT_METHOD_BLANK(1012, "PAYPAL_PAYMENT_METHOD_BLANK", HttpStatus.BAD_REQUEST),
    PAYPAL_DESCRIPTION_BLANK(1013, "PAYPAL_DESCRIPTION_BLANK", HttpStatus.BAD_REQUEST),
    PAYPAL_CANCEL_URL_BLANK(1014, "PAYPAL_CANCEL_URL_BLANK", HttpStatus.BAD_REQUEST),
    PAYPAL_SUCCESS_URL_BLANK(1015, "PAYPAL_SUCCESS_URL_BLANK", HttpStatus.BAD_REQUEST),
    PAYPAL_CANCEL_URL_INVALID(1016, "PAYPAL_CANCEL_URL_INVALID", HttpStatus.BAD_REQUEST),
    PAYPAL_SUCCESS_URL_INVALID(1017, "PAYPAL_SUCCESS_URL_INVALID", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

}