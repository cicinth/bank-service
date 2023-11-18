package com.transfer.transferservice.exceptions;

import org.springframework.http.HttpStatus;

public class BankNotFound extends AbstractException{

    public BankNotFound(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}