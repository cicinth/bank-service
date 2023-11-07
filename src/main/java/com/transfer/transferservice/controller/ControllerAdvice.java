package com.transfer.transferservice.controller;

import com.transfer.transferservice.exceptions.AbstractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<String> handleAbstractException(AbstractException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(exception.getMessage());
    }


}
