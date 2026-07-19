package com.cinejunction.rating.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RatingAlreadyExistsException extends RuntimeException {

    public RatingAlreadyExistsException(String message) {
        super(message);
    }
}
