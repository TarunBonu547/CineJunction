package com.cinejunction.tmdb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TMDbUnauthorizedException extends RuntimeException {

    public TMDbUnauthorizedException(String message) {
        super(message);
    }
}
