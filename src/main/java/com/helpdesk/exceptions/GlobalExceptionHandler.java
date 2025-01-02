package com.helpdesk.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.helpdesk.exceptions.resources.ResourceCreationException;
import com.helpdesk.exceptions.resources.ResourceNotFoundException;
import com.helpdesk.exceptions.resources.ResourceUpdateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });
        
        response.put("message", "The given data was invalid.");
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(violation.getMessage());
        });
        
        response.put("message", "The given data was invalid.");
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.error("Authorization denied: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are not authorized to perform this action.");
        response.put("details", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler({ResourceCreationException.class, ResourceUpdateException.class})
    public ResponseEntity<Map<String, Object>> handleResourceOperation(Exception ex) {
        log.error("Resource operation failed: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();
        
        String[] errorParts = ex.getMessage().split(";");
        for (String error : errorParts) {
            String[] parts = error.trim().split(":", 2);
            if (parts.length == 2) {
                String field = parts[0].trim();
                String message = parts[1].trim();
                errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
            }
        }

        response.put("message", "The given data was invalid.");
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();
        
        if (ex.getMessage().contains("rawPassword")) {
            errors.computeIfAbsent("password", k -> new ArrayList<>())
                 .add("Password is required");
        } else {
            errors.computeIfAbsent("general", k -> new ArrayList<>())
                 .add(ex.getMessage());
        }
        
        response.put("message", "The given data was invalid.");
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalExceptions(RuntimeException ex) {
        log.error("Illegal state/argument error: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, List<String>> errors = new HashMap<>();
        
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains(":")) {
                String[] parts = message.split(":", 2);
                String field = parts[0].trim().toLowerCase();
                String errorMessage = parts[1].trim();
                errors.computeIfAbsent(field, k -> new ArrayList<>()).add(errorMessage);
            } else {
                errors.computeIfAbsent("general", k -> new ArrayList<>()).add(message);
            }
        }
        
        response.put("message", "The given data was invalid.");
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Multipart exception: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "The given data was invalid.");
        response.put("errors", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        log.error("No resource found: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}