package com.tien.promotion.exception;

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

      FILE_UPLOAD_FAILED(2000, "File upload failed. Please try uploading the file again.", HttpStatus.SERVICE_UNAVAILABLE),
      SHOP_NOT_FOUND(2001, "The shop could not be found. Please check the shop details and try again.", HttpStatus.NOT_FOUND),
      PRODUCT_NOT_FOUND(2002, "The product could not be found. Please check the product details and try again.", HttpStatus.NOT_FOUND),
      PROMOTION_NOT_FOUND(2003, "The promotion could not be found. Please verify the promotion details.", HttpStatus.NOT_FOUND),
      PROMOTION_EXPIRED(2004, "The promotion has expired. Please check the promotion dates and try again.", HttpStatus.BAD_REQUEST),
      PROMOTION_USAGE_LIMIT_REACHED(2005, "The promotion usage limit has been reached. No more uses are allowed.", HttpStatus.BAD_REQUEST),
      CART_NOT_FOUND(2006, "The shopping cart could not be found. Please verify your cart and try again.", HttpStatus.NOT_FOUND),
      PRODUCT_NOT_ELIGIBLE_FOR_PROMO(2007, "The product is not eligible for the promotion. Please check the eligibility criteria.", HttpStatus.BAD_REQUEST),
      NO_ELIGIBLE_PRODUCTS(2008, "There are no eligible products for the promotion. Please verify the products in your cart.", HttpStatus.BAD_REQUEST),
      ORDER_VALUE_TOO_LOW(2009, "The order value is too low. Please ensure the order meets the minimum value required for the promotion.", HttpStatus.BAD_REQUEST),
      NAME_IS_REQUIRED(2010, "The name is required. Please provide a shop name.", HttpStatus.BAD_REQUEST),
      PROMO_CODE_IS_REQUIRED(2011, "Promo code is required. Please provide a valid promo code.", HttpStatus.BAD_REQUEST),
      TYPE_IS_REQUIRED(2012, "Type is required. Please select a valid promotion type.", HttpStatus.BAD_REQUEST),
      CONDITIONS_ARE_REQUIRED(2013, "Conditions are required. Please define the conditions for the promotion.", HttpStatus.BAD_REQUEST),
      DISCOUNT_IS_REQUIRED(2014, "Discount information is required. Please provide a discount.", HttpStatus.BAD_REQUEST),
      USAGE_LIMIT_IS_REQUIRED(2015, "Usage limit is required. Please specify the usage limit for the promotion.", HttpStatus.BAD_REQUEST),
      START_DATE_IS_REQUIRED(2016, "Start date is required. Please specify the start date of the promotion.", HttpStatus.BAD_REQUEST),
      END_DATE_IS_REQUIRED(2017, "End date is required. Please specify the end date of the promotion.", HttpStatus.BAD_REQUEST),
      AMOUNT_IS_REQUIRED(2018, "Amount is required for the discount. Please specify the discount amount.", HttpStatus.BAD_REQUEST),
      PERCENTAGE_IS_REQUIRED(2019, "Percentage is required for the discount. Please provide a discount percentage.", HttpStatus.BAD_REQUEST),
      MAX_DISCOUNT_VALUE_CANNOT_BE_NEGATIVE(2020, "Maximum discount value cannot be negative. Please provide a valid maximum discount.", HttpStatus.BAD_REQUEST),
      MIN_ORDER_VALUE_CANNOT_BE_NEGATIVE(2021, "Minimum order value cannot be negative. Please provide a valid minimum order value.", HttpStatus.BAD_REQUEST),
      NAME_INVALID_SIZE(2022, "The shop name must be between 3 and 255 characters.", HttpStatus.BAD_REQUEST),
      PROMO_CODE_INVALID_SIZE(2023, "The promo code must be between 5 and 20 characters.", HttpStatus.BAD_REQUEST),
      AMOUNT_CANNOT_BE_NEGATIVE(2024, "The discount amount cannot be negative. Please provide a valid amount.", HttpStatus.BAD_REQUEST),
      PERCENTAGE_CANNOT_BE_NEGATIVE(2025, "The discount percentage cannot be negative. Please provide a valid percentage.", HttpStatus.BAD_REQUEST),
      MAX_DISCOUNT_VALUE_IS_REQUIRED(2026, "The maximum discount value is required. Please provide a valid value.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}