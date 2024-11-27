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

      SUBSCRIPTION_CANCEL_FAILED(2000, "Failed to cancel subscription. Please try again later.", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_SESSION_CREATION_FAILED(2001, "Failed to create subscription session. Please verify your details and try again.", HttpStatus.BAD_REQUEST),
      PAYMENT_SESSION_CREATION_FAILED(2002, "Failed to create payment session. Please try again later.", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CREATION_FAILED(2003, "Failed to create subscription. Please verify your details and try again.", HttpStatus.BAD_REQUEST),
      PAYMENT_FAILED(2004, "Payment failed. Please verify your payment details and try again.", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(2005, "Email is required. Please provide a valid email address.", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(2006, "Invalid email address. Please enter a valid email.", HttpStatus.BAD_REQUEST),
      PACKAGE_TYPE_IS_REQUIRED(2007, "Package type is required. Please select a valid package.", HttpStatus.BAD_REQUEST),
      AMOUNT_IS_REQUIRED(2008, "Amount is required. Please provide the payment amount.", HttpStatus.BAD_REQUEST),
      AMOUNT_MUST_BE_POSITIVE(2009, "Amount must be positive. Please provide a valid amount.", HttpStatus.BAD_REQUEST),
      PRODUCT_NAME_IS_REQUIRED(2010, "Product name is required. Please specify the product name.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}