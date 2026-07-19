package com.cinejunction.movieperson.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MoviePersonNotFoundException extends RuntimeException {

    public MoviePersonNotFoundException(String message) {
        super(message);
    }
}
