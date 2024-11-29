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

      PRODUCT_NOT_FOUND(2000, "The product you requested was not found. Please check the product ID and try again.", HttpStatus.NOT_FOUND),
      CART_NOT_FOUND(2001, "No cart found for the provided user. Please check the user details and try again.", HttpStatus.NOT_FOUND),
      REDIS_SAVE_FAILED(2002, "Failed to save data to Redis. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
      TOTAL_CALCULATION_FAILED(2003, "Failed to calculate the total order amount. Please check the items and try again.", HttpStatus.BAD_REQUEST),
      PRODUCT_FETCH_FAILED(2004, "Failed to fetch product details. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
      ORDER_CREATION_FAILED(2005, "Failed to create the order. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
      USERNAME_IS_REQUIRED(2006, "Username is required. Please provide a valid username.", HttpStatus.BAD_REQUEST),
      USERNAME_INVALID(2007, "The provided username is invalid. Please check the format and try again.", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(2008, "Email address is required. Please provide a valid email.", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(2009, "The provided email is invalid. Please check the email format.", HttpStatus.BAD_REQUEST),
      PRODUCTS_LIST_CANNOT_BE_EMPTY(2010, "The product list cannot be empty. Please add products to the cart.", HttpStatus.BAD_REQUEST),
      ITEMS_CANNOT_BE_EMPTY(2011, "The items in the cart cannot be empty. Please add at least one item.", HttpStatus.BAD_REQUEST),
      TOTAL_MUST_BE_POSITIVE(2012, "The total amount must be a positive value.", HttpStatus.BAD_REQUEST),
      STATUS_IS_REQUIRED(2013, "Order status is required. Please specify a valid status.", HttpStatus.BAD_REQUEST),
      PRODUCT_ID_IS_REQUIRED(2014, "Product ID is required. Please provide a valid product ID.", HttpStatus.BAD_REQUEST),
      QUANTITY_IS_REQUIRED(2015, "Quantity is required. Please provide a valid quantity.", HttpStatus.BAD_REQUEST),
      QUANTITY_MUST_BE_POSITIVE(2016, "Quantity must be a positive value. Please correct the quantity.", HttpStatus.BAD_REQUEST),
      PRODUCT_SERVICE_UNAVAILABLE(2017, "Product service is currently unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
      ORDER_SERVICE_UNAVAILABLE(2018, "Order service is currently unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
      OUT_OF_STOCK(2019, "The requested product is out of stock. Please check availability and try again.", HttpStatus.BAD_REQUEST),
      CANNOT_ADD_OWN_PRODUCT(2020, "You cannot add your own product to the cart.", HttpStatus.BAD_REQUEST),
      DISCOUNT_ALREADY_APPLIED(2021, "Discount has already been applied to this order.", HttpStatus.BAD_REQUEST),
      INVALID_OPERATION(2022, "The operation is invalid. Please review and try again.", HttpStatus.BAD_REQUEST),
      ORDER_VALUE_TOO_LOW(2023, "The order value is too low. Please ensure the total meets the minimum requirement.", HttpStatus.BAD_REQUEST),
      QUANTITY_MUST_BE_AT_LEAST_1(2024, "Quantity must be at least 1.", HttpStatus.BAD_REQUEST),
      VARIANT_ID_IS_REQUIRED(2025, "Variant ID is required. Please provide a valid variant ID.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}