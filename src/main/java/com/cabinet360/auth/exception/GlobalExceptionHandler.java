package com.cabinet360.auth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ERROR");
        body.put("message", message);
        body.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Print full message for debugging
        System.err.println("🔍 Full DB error: " + ex.getMostSpecificCause().getMessage());

        String message = "Duplicate entry";
        String lowerMsg = ex.getMostSpecificCause().getMessage().toLowerCase();

        if (lowerMsg.contains("email")) {
            message = "Email already exists";
        } else if (lowerMsg.contains("medical_license_number")) {
            message = "Medical license number already exists";
        } else if (lowerMsg.contains("phone_number")) {
            message = "Phone number already exists";
        } else if (lowerMsg.contains("national_id")) {
            message = "National ID already exists";
        }

        return buildErrorResponse(message, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        ex.printStackTrace(); // for debugging in console
        System.err.println("⚠️ Caught in generic handler: " + ex.getClass().getName());
        return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();
        body.put("status", "ERROR");
        body.put("message", fieldErrors);
        body.put("timestamp", Instant.now().toString());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
