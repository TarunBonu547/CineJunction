package com.cinejunction.tmdb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TMDbRateLimitException extends RuntimeException {

    public TMDbRateLimitException(String message) {
        super(message);
    }
}
