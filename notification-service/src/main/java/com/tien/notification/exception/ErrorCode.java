package com.tien.notification.exception;

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

      CANNOT_SEND_EMAIL(2000, "Unable to send email due to an internal error.", HttpStatus.BAD_REQUEST),
      RECIPIENT_IS_REQUIRED(2001, "Recipient email is required. Please provide a valid recipient email.", HttpStatus.BAD_REQUEST),
      SUBJECT_IS_REQUIRED(2002, "Subject is required. Please provide a subject for the email.", HttpStatus.BAD_REQUEST),
      SUBJECT_SIZE_CONSTRAINT(2003, "Subject must be between 1 and 255 characters.", HttpStatus.BAD_REQUEST),
      CONTENT_IS_REQUIRED(2004, "Email content is required. Please provide the body of the email.", HttpStatus.BAD_REQUEST),
      EMAIL_IS_REQUIRED(2005, "Email is required. Please provide a valid email address.", HttpStatus.BAD_REQUEST),
      INVALID_EMAIL(2006, "Invalid email format. Please provide a valid email address.", HttpStatus.BAD_REQUEST),
      SENDER_IS_REQUIRED(2007, "Sender information is required. Please provide sender name and email.", HttpStatus.BAD_REQUEST),
      RECIPIENTS_ARE_REQUIRED(2008, "At least one recipient is required. Please provide a valid recipient.", HttpStatus.BAD_REQUEST),
      AT_LEAST_ONE_RECIPIENT_IS_REQUIRED(2009, "There must be at least one recipient to send the email.", HttpStatus.BAD_REQUEST),
      SENDER_NAME_IS_REQUIRED(2010, "Sender name is required. Please provide the sender's name.", HttpStatus.BAD_REQUEST),
      SENDER_EMAIL_IS_REQUIRED(2011, "Sender email is required. Please provide the sender's email address.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}