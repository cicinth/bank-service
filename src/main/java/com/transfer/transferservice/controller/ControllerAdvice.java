package com.transfer.transferservice.controller;

import com.transfer.transferservice.controller.dto.ErrorResponse;
import com.transfer.transferservice.exceptions.AbstractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> handleAbstractException(AbstractException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorResponse(exception.getStatus().getReasonPhrase(), exception.getMessage()));
    }

}
