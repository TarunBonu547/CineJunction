package com.cinejunction.rating.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedRatingAccessException extends RuntimeException {

    public UnauthorizedRatingAccessException(String message) {
        super(message);
    }
}
