package com.cinejunction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a genre with the same name already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class GenreAlreadyExistsException extends RuntimeException {

    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
