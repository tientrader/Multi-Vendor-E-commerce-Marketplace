package com.tien.post.exception;

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

      USER_NOT_EXISTED(2000, "The user does not exist. Please check the provided user ID or email.", HttpStatus.NOT_FOUND),
      PROFILE_NOT_FOUND(2001, "The user profile could not be found.", HttpStatus.NOT_FOUND),
      POST_NOT_FOUND(2002, "The post could not be found. Please check the post ID.", HttpStatus.NOT_FOUND),
      LIKE_NOT_FOUND(2003, "The like could not be found. Please check if the post has been liked.", HttpStatus.NOT_FOUND),
      COMMENT_NOT_FOUND(2004, "The comment could not be found. Please check the comment ID.", HttpStatus.NOT_FOUND),
      USER_NOT_VIP(2005, "The user is not a VIP. Please ensure the user meets the VIP requirements.", HttpStatus.BAD_REQUEST),
      FILE_UPLOAD_FAILED(2006, "The file upload failed due to a server error. Please try again.", HttpStatus.SERVICE_UNAVAILABLE),
      CONTENT_IS_REQUIRED(2007, "Content is required. Please provide content for the post.", HttpStatus.BAD_REQUEST),
      CONTENT_SIZE_CONSTRAINT(2008, "Content must be between 1 and 500 characters.", HttpStatus.BAD_REQUEST),
      ;

      int code;
      String message;
      HttpStatusCode statusCode;

}