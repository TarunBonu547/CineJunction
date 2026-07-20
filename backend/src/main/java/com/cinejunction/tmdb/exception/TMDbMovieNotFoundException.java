package com.cinejunction.tmdb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TMDbMovieNotFoundException extends RuntimeException {

    public TMDbMovieNotFoundException(String message) {
        super(message);
    }
}
