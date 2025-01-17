package com.tien.cart.exception;

import com.tien.cart.dto.ApiResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

      @ExceptionHandler(Exception.class)
      ResponseEntity<ApiResponse<Object>> handlingException(Exception exception) {
            log.error("Unexpected error occurred: ", exception);
            return buildResponse(ErrorCode.UNCATEGORIZED_EXCEPTION, ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
      }

      @ExceptionHandler(AccessDeniedException.class)
      ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
            log.warn("Access denied: {}", exception.getMessage());
            return buildResponse(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getMessage());
      }

      @ExceptionHandler(AppException.class)
      ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
            log.warn("Application-specific exception occurred: {}", exception.getErrorCode().getMessage());
            return buildResponse(exception.getErrorCode(), exception.getErrorCode().getMessage());
      }

      @ExceptionHandler(FeignException.class)
      ResponseEntity<ApiResponse<Object>> handleFeignException(FeignException exception) {
            log.error("Feign exception occurred: {}", exception.getMessage());
            return buildResponse(ErrorCode.EXTERNAL_SERVICE_ERROR, ErrorCode.EXTERNAL_SERVICE_ERROR.getMessage());
      }

      @ExceptionHandler(MaxUploadSizeExceededException.class)
      ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
            log.error("File upload exceeds maximum size limit: {}", exception.getMessage());
            return buildResponse(ErrorCode.FILE_SIZE_EXCEEDED, ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
      }

      @ExceptionHandler(MissingServletRequestParameterException.class)
      ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
            log.error("Missing required request parameter: {}", exception.getMessage());
            return buildResponse(ErrorCode.MISSING_REQUIRED_PARAMETER, ErrorCode.MISSING_REQUIRED_PARAMETER.getMessage());
      }

      @ExceptionHandler(DataIntegrityViolationException.class)
      ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
            String message = exception.getMessage();

            Map<String, ErrorCode> errorCodeMap = Map.of(
                    "Duplicate entry", ErrorCode.DUPLICATE_ENTRY,
                    "foreign key constraint fails", ErrorCode.FOREIGN_KEY_VIOLATION
            );

            for (Map.Entry<String, ErrorCode> entry : errorCodeMap.entrySet()) {
                  if (message.contains(entry.getKey())) {
                        log.error("{}: {}", entry.getValue().getMessage(), message);
                        return buildResponse(entry.getValue(), entry.getValue().getMessage());
                  }
            }

            log.error("Data integrity violation occurred: {}", message);
            return buildResponse(ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage());
      }

      @ExceptionHandler(MethodArgumentNotValidException.class)
      ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {
            String enumKey = Optional.ofNullable(exception.getFieldError())
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .orElse("INVALID_KEY");

            AtomicReference<ErrorCode> errorCode = new AtomicReference<>(ErrorCode.INVALID_KEY);
            Optional.of(enumKey)
                    .flatMap(key -> {
                          try {
                                errorCode.set(ErrorCode.valueOf(key));
                          } catch (IllegalArgumentException e) {
                                return Optional.empty();
                          }
                          return exception.getBindingResult()
                                  .getAllErrors()
                                  .stream()
                                  .map(error -> (ConstraintViolation<?>) error.unwrap(ConstraintViolation.class))
                                  .findFirst()
                                  .map(violation -> violation.getConstraintDescriptor().getAttributes());
                    });

            String message = errorCode.get().getMessage();

            log.error("Validation failed with error: {} - {}", enumKey, message);
            return buildResponse(errorCode.get(), message);
      }

      private ResponseEntity<ApiResponse<Object>> buildResponse(ErrorCode errorCode, String message) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(errorCode.getCode())
                    .message(message)
                    .build();
            return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
      }

}