package com.razdeep.konsignapi.exception;

import com.razdeep.konsignapi.model.KonsignApiResponse;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<KonsignApiResponse> handleBadCredentials(BadCredentialsException ex) {

        LOG.info("bad credential exception", ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new KonsignApiResponse(false, "Username or password mismatch", null));
    }

    @ExceptionHandler(UsernameAlreadyExists.class)
    public ResponseEntity<KonsignApiResponse> handleUsernameAlreadyExists(UsernameAlreadyExists ex) {

        LOG.info("User name already exists", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new KonsignApiResponse(false, "Username already exists", null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<KonsignApiResponse> handleUserNotFound(UserNotFoundException ex) {

        LOG.info("User not found", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new KonsignApiResponse(false, "User not found", null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<KonsignApiResponse> handleUnauthorizedException(UnauthorizedException ex) {

        LOG.info("Unauthorized exception", ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new KonsignApiResponse(false, "Unauthorized", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<KonsignApiResponse> handleGenericException(Exception ex) {

        LOG.error("Unexpected error", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new KonsignApiResponse(false, "An unexpected error occurred", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<KonsignApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> missingFields = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> missingFields.add(error.getField()));

        String message = String.join(",", missingFields) + " not properly passed";

        return ResponseEntity.badRequest()
                .body(KonsignApiResponse.builder()
                        .success(false)
                        .message(message)
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<KonsignApiResponse> handleValidationErrors(ResourceNotFoundException ex) {

        LOG.info("Resource not found", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new KonsignApiResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(SaveResourceException.class)
    public ResponseEntity<KonsignApiResponse> handleValidationErrors(SaveResourceException ex) {

        LOG.info("Resource could not be saved", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new KonsignApiResponse(false, ex.getMessage(), null));
    }
}
