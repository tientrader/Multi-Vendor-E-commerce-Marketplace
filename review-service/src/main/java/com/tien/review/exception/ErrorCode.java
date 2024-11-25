package com.tien.review.exception;

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

      PRODUCT_NOT_FOUND(2001, "The product was not found. Please check the product ID and try again.", HttpStatus.NOT_FOUND),
      USER_NOT_PURCHASED_PRODUCT(2002, "You cannot review a product you have not purchased.", HttpStatus.FORBIDDEN),
      ALREADY_REVIEWED(2003, "You have already reviewed this product. Multiple reviews are not allowed.", HttpStatus.FORBIDDEN),
      FILE_UPLOAD_FAILED(2004, "File upload failed. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
      PRODUCT_ID_IS_REQUIRED(2005, "Product ID is required. Please provide a valid product ID.", HttpStatus.BAD_REQUEST),
      VARIANT_ID_IS_REQUIRED(2006, "Variant ID is required. Please provide a valid variant ID.", HttpStatus.BAD_REQUEST),
      RATING_IS_REQUIRED(2007, "Rating is required. Please provide a rating between 1 and 5.", HttpStatus.BAD_REQUEST),
      RATING_TOO_LOW(2008, "Rating is too low. The minimum rating allowed is 1.", HttpStatus.BAD_REQUEST),
      RATING_TOO_HIGH(2009, "Rating is too high. The maximum rating allowed is 5.", HttpStatus.BAD_REQUEST),
      CONTENT_TOO_LONG(2010, "Content is too long. Please limit the content to 500 characters.", HttpStatus.BAD_REQUEST),
      REVIEW_NOT_FOUND(2011, "The review was not found. Please ensure the review ID is correct.", HttpStatus.NOT_FOUND),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}