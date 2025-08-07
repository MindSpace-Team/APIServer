package com.MindSpaceTeam.MindSpace.Advice;

import com.MindSpaceTeam.MindSpace.Exception.ExternalConnectionException;
import com.MindSpaceTeam.MindSpace.Exception.InvalidJsonFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.warn("Runtime exception", e);
        return ResponseEntity.internalServerError().body("Internal server error");
    }

    @ExceptionHandler(InvalidJsonFormatException.class)
    public ResponseEntity<String> handleJsonFormatException(InvalidJsonFormatException e) {
        log.warn("Json format exception", e);
        return ResponseEntity.internalServerError().body("Internal server error");
    }

    @ExceptionHandler(ExternalConnectionException.class)
    public ResponseEntity<String> handleExternalConnectionException(ExternalConnectionException e) {
        log.warn("External connection exception", e);
        return ResponseEntity.internalServerError().body("Internal server error");
    }

}
