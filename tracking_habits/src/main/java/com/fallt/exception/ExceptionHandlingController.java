package com.fallt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthorizationException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(body);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundAlreadyExistException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(DBException.class)
    public ResponseEntity<ExceptionResponse> handleDbException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}
