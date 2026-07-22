package com.cinejunction.userlist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedListAccessException extends RuntimeException {
    public UnauthorizedListAccessException(String message) {
        super(message);
    }
}
