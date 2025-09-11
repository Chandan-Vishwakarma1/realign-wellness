package com.realignwellness.exception;

import com.realignwellness.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b)->a));
        ApiResponse<Map<String, String>> body = ApiResponse.<Map<String, String>>builder()
                .success(false).code("VALIDATION_ERROR").message("Validation failed").timestamp(Instant.now()).data(errors).build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDup(DuplicateResourceException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false).code("DUPLICATE").message(ex.getMessage()).timestamp(Instant.now()).data(null).build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false).code("NOT_FOUND").message(ex.getMessage()).timestamp(Instant.now()).data(null).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false).code("UNAUTHORIZED").message(ex.getMessage()).timestamp(Instant.now()).data(null).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
