package com.transfer.transferservice.exceptions;

import org.springframework.http.HttpStatus;

public class ValueNotAllowed  extends AbstractException {
    public ValueNotAllowed(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
