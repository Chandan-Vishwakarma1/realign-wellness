package com.realignwellness.exception;

import com.realignwellness.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
//                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b)->a));
//        ApiResponse<Map<String, String>> body = ApiResponse.<Map<String, String>>builder()
//                .success(false).code("VALIDATION_ERROR").message("Validation failed").timestamp(Instant.now()).data(errors).build();
//        return ResponseEntity.badRequest().body(body);
//    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("BAD_REQUEST")
                .message("Validation failed")
                .timestamp(Instant.now())
                .data(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder().success(false).code("INVALID_CREDENTIAL")
                .message(ex.getMessage()).timestamp(Instant.now()).data(null).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder().success(false).code("BAD_REQUEST").message(ex.getMessage()).timestamp(Instant.now()).data(null).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ApiResponse<Object>> handleIOException(IOException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("Internal Server Error: Problem writing response - " + ex.getMessage())
                .timestamp(Instant.now())
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(Instant.now())
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("BAD_REQUEST")
                .message("Invalid argument: " + ex.getMessage())
                .timestamp(Instant.now())
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Token expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("TOKEN_EXPIRED")
                .message("JWT token has expired")
                .timestamp(Instant.now())
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Generic JWT errors
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(io.jsonwebtoken.JwtException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.builder()
                .success(false)
                .code("INVALID_TOKEN")
                .message("JWT token is invalid")
                .timestamp(Instant.now())
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(org.springframework.mail.MailSendException.class)
    public ResponseEntity<ApiResponse<Void>> handleMail(MailSendException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ApiResponse.<Void>builder().success(false).code("MAIL_SEND_FAILED")
                        .message("Email provider is unavailable. Please try again later.")
                        .timestamp(Instant.now()).data(null).build()
        );
    }
}
