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

      PRODUCT_NOT_FOUND(2000, "The specified product could not be found.", HttpStatus.NOT_FOUND),
      CATEGORY_NOT_FOUND(2001, "The specified category could not be found.", HttpStatus.NOT_FOUND),
      OUT_OF_STOCK(2002, "The requested product is out of stock.", HttpStatus.BAD_REQUEST),
      SHOP_NOT_FOUND(2003, "The specified shop could not be found.", HttpStatus.NOT_FOUND),
      PRODUCT_NAME_IS_REQUIRED(2004, "Product name is required. Please provide a valid name.", HttpStatus.BAD_REQUEST),
      PRICE_MUST_BE_POSITIVE(2005, "The price must be a positive value.", HttpStatus.BAD_REQUEST),
      STOCK_MUST_BE_POSITIVE(2006, "Stock quantity must be a positive number.", HttpStatus.BAD_REQUEST),
      CATEGORY_ID_IS_REQUIRED(2007, "Category ID is required. Please provide a valid category ID.", HttpStatus.BAD_REQUEST),
      CATEGORY_NAME_IS_REQUIRED(2008, "Category name is required. Please provide a valid name.", HttpStatus.BAD_REQUEST),
      VARIANT_NOT_FOUND(2009, "The specified product variant could not be found.", HttpStatus.NOT_FOUND),
      STOCK_IS_REQUIRED(2010, "Stock quantity is required. Please provide the stock value.", HttpStatus.BAD_REQUEST),
      PRICE_IS_REQUIRED(2011, "Price is required. Please provide a valid price.", HttpStatus.BAD_REQUEST),
      IMAGE_REQUIRED(2012, "Product image is required. Please upload a valid image.", HttpStatus.BAD_REQUEST),
      FILE_UPLOAD_FAILED(2013, "Failed to upload the file. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
      PRODUCT_NAME_MUST_NOT_EXCEED(2014, "Product name must not exceed 255 characters.", HttpStatus.BAD_REQUEST),
      DESCRIPTION_MUST_NOT_EXCEED(2015, "Product description must not exceed 1000 characters.", HttpStatus.BAD_REQUEST),
      VARIANTS_ARE_REQUIRED(2016, "At least one product variant is required.", HttpStatus.BAD_REQUEST),
      AT_LEAST_ONE_VARIANT_IS_REQUIRED(2017, "Please provide at least one valid variant.", HttpStatus.BAD_REQUEST),
      ATTRIBUTES_ARE_REQUIRED(2018, "Product attributes are required. Please include them.", HttpStatus.BAD_REQUEST),
      AT_LEAST_ONE_ATTRIBUTE_IS_REQUIRED(2019, "At least one attribute is required for the product variant.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}