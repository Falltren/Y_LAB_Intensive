package com.fallt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler({EntityNotFoundException.class, ValidationException.class})
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .error("BAD REQUEST")
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ExceptionResponse> handleSecurityException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .error("UNAUTHORIZED")
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundAlreadyExistException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .error("ALREADY EXIST")
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(DBException.class)
    public ResponseEntity<ExceptionResponse> handleDbException(Exception e) {
        ExceptionResponse body = ExceptionResponse.builder()
                .error("DATABASE EXCEPTION")
                .timestamp(System.currentTimeMillis())
                .errorDescription(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

}
