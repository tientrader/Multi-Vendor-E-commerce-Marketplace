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

      UNCATEGORIZED_EXCEPTION(1000, "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
      INVALID_KEY(1001, "The provided key is invalid. Please check the key and try again.", HttpStatus.BAD_REQUEST),
      UNAUTHENTICATED(1002, "You are not authenticated. Please log in first.", HttpStatus.UNAUTHORIZED),
      UNAUTHORIZED(1003, "You are not authorized to perform this action.", HttpStatus.UNAUTHORIZED),
      EXTERNAL_SERVICE_ERROR(1004, "There was an issue with an external service. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
      DATA_INTEGRITY_VIOLATION(1005, "Data integrity violation. The operation cannot be completed due to conflicting data.", HttpStatus.BAD_REQUEST),
      DUPLICATE_ENTRY(1006, "The entry already exists. Please check for duplicates and try again.", HttpStatus.BAD_REQUEST),
      FOREIGN_KEY_VIOLATION(1007, "Foreign key violation. Please ensure all related data exists before proceeding.", HttpStatus.BAD_REQUEST),
      FILE_SIZE_EXCEEDED(1008, "The file size exceeds the allowed limit. Please upload a smaller file.", HttpStatus.BAD_REQUEST),
      MISSING_REQUIRED_PARAMETER(1009, "A required parameter is missing. Please ensure all required fields are provided.", HttpStatus.BAD_REQUEST),
      SERVICE_UNAVAILABLE(1010, "The service is currently unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),

      PRODUCT_NOT_FOUND(2000, "Product not found. Please check the product ID and try again.", HttpStatus.NOT_FOUND),
      ORDER_NOT_FOUND(2001, "Order not found. Please check the order ID and try again.", HttpStatus.NOT_FOUND),
      ORDER_IS_NOT_YOURS(2002, "You are not authorized to access this order. It belongs to another user.", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(2003, "Email address is required for this operation.", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(2004, "The provided email address is invalid. Please provide a valid email.", HttpStatus.BAD_REQUEST),
      ITEMS_CANNOT_BE_EMPTY(2005, "The order items cannot be empty. Please add at least one item.", HttpStatus.BAD_REQUEST),
      PRODUCT_ID_IS_REQUIRED(2006, "Product ID is required. Please provide a valid product ID.", HttpStatus.BAD_REQUEST),
      QUANTITY_IS_REQUIRED(2007, "Quantity is required. Please specify a quantity.", HttpStatus.BAD_REQUEST),
      QUANTITY_MUST_BE_POSITIVE(2008, "Quantity must be greater than zero. Please provide a positive value.", HttpStatus.BAD_REQUEST),
      OUT_OF_STOCK(2009, "The product is out of stock. Please check availability and try again.", HttpStatus.BAD_REQUEST),
      VARIANT_ID_IS_REQUIRED(2010, "Variant ID is required. Please provide a valid variant ID.", HttpStatus.BAD_REQUEST),
      PAYMENT_FAIL(2011, "Payment failed. Please check your payment details and try again.", HttpStatus.BAD_REQUEST),
      MORE_THAN_ONE_PRODUCT(2012, "The order contains more than one product. Please place individual orders for each product.", HttpStatus.BAD_REQUEST),
      PAYMENT_METHOD_IS_REQUIRED(2013, "Payment method is required. Please specify a valid payment method.", HttpStatus.BAD_REQUEST),
      PAYMENT_METHOD_MUST_BE_ONE_OF_CARD_CODE(2014, "The payment method must be either CARD or COD.", HttpStatus.BAD_REQUEST),
      QUANTITY_MUST_BE_AT_LEAST_1(2015, "Quantity must be at least 1. Please specify a valid quantity.", HttpStatus.BAD_REQUEST),
      PAYMENT_TOKEN_IS_REQUIRED(2015, "Payment token is required. Please specify a valid payment token.", HttpStatus.BAD_REQUEST),
      INVALID_PAYMENT_METHOD(2016, "The provided payment method is invalid. Please provide a valid method.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}