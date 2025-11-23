package com.ezh.Inventory.utils.exception;

import com.ezh.Inventory.utils.common.ResponseResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice("/")
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseResource<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("Service Exception occurred: {}", ex.getMessage());
        return ResponseResource.error(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResource<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred {}", ex.getMessage());
        return ResponseResource.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResource<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("Validation failed: {}", errorMessage);
        return ResponseResource.error(HttpStatus.BAD_REQUEST, errorMessage);
    }
}
