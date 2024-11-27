package com.tien.user.exception;

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

      INVALID_USERNAME(2000, "Username must be at least 4 characters.", HttpStatus.BAD_REQUEST),
      INVALID_PASSWORD(2001, "Password must be at least 8 characters.", HttpStatus.BAD_REQUEST),
      EMAIL_EXISTED(2002, "The email address is already registered. Please use a different one.", HttpStatus.BAD_REQUEST),
      USER_EXISTED(2003, "The user already exists. Please choose a different username.", HttpStatus.BAD_REQUEST),
      USERNAME_IS_MISSING(2004, "Username is required. Please provide a username.", HttpStatus.BAD_REQUEST),
      PROFILE_NOT_FOUND(2005, "Profile not found. Please ensure the user exists.", HttpStatus.NOT_FOUND),
      USER_NOT_EXISTED(2006, "User does not exist. Please check the username and try again.", HttpStatus.NOT_FOUND),
      DOB_IS_REQUIRED(2007, "Date of birth is required. Please provide your date of birth.", HttpStatus.BAD_REQUEST),
      INVALID_DOB(2008, "Please enter a valid date of birth", HttpStatus.BAD_REQUEST),
      FIRSTNAME_IS_REQUIRED(2009, "First name is required. Please provide your first name.", HttpStatus.BAD_REQUEST),
      LASTNAME_IS_REQUIRED(2010, "Last name is required. Please provide your last name.", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(2011, "The email address is invalid. Please enter a valid email.", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(2012, "Email is required. Please provide an email address.", HttpStatus.BAD_REQUEST),
      LOGIN_FAILED(2013, "Login failed. Please check your credentials and try again.", HttpStatus.BAD_REQUEST),
      INVALID_USERNAME_OR_PASSWORD_OR_OTP(2014, "Invalid username, password or OTP. Please check your login credentials.", HttpStatus.BAD_REQUEST),
      INVALID_PRICE_ID(2015, "The provided price ID is invalid. Please check the price details.", HttpStatus.BAD_REQUEST),
      USER_NOT_VIP(2016, "The user is not a VIP. Access restricted to VIP users only.", HttpStatus.BAD_REQUEST),
      PHONE_NUMBER_IS_REQUIRED(2017, "Phone number is required. Please provide your phone number.", HttpStatus.BAD_REQUEST),
      INVALID_PHONE_NUMBER(2018, "The phone number provided is invalid. Please provide a valid phone number.", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CANCELLATION_FAILED(2019, "Subscription cancellation failed. Please try again later.", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_CREATION_FAILED(2020, "Subscription creation failed. Please try again later.", HttpStatus.BAD_REQUEST),
      INVALID_PACKAGE_TYPE(2021, "The package type provided is invalid. Please select a valid package.", HttpStatus.BAD_REQUEST),
      SUBSCRIPTION_SESSION_CREATION_FAILED(2022, "Failed to create a subscription session. Please try again later.", HttpStatus.BAD_REQUEST),
      INVALID_STRIPE_SIGNATURE(2023, "The Stripe signature is invalid. Please check your payment details.", HttpStatus.BAD_REQUEST),
      PHONE_NUMBER_EXISTED(2024, "The phone number is already registered. Please use a different number.", HttpStatus.BAD_REQUEST),
      SESSION_NOT_ACTIVE(2025, "Session is not active. Please log in again to continue.", HttpStatus.UNAUTHORIZED),
      NO_ACTIVE_SUBSCRIPTION(2026, "No active subscription found. Please subscribe to continue.", HttpStatus.UNAUTHORIZED),
      REFRESH_TOKEN_IS_REQUIRED(2027, "Refresh token is required. Please provide a valid refresh token.", HttpStatus.BAD_REQUEST),
      PACKAGE_TYPE_IS_REQUIRED(2028, "Package type is required. Please provide a valid package type.", HttpStatus.BAD_REQUEST),
      INVALID_OTP(2029, "The OTP provided is invalid. Please check and try again.", HttpStatus.BAD_REQUEST),
      PASSWORD_POLICY_VIOLATION(2030, "Password must meet the required policy: min length 8, at least one uppercase letter, one number, and one special character.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}