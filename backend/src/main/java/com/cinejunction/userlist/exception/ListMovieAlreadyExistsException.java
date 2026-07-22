package com.cinejunction.userlist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ListMovieAlreadyExistsException extends RuntimeException {
    public ListMovieAlreadyExistsException(String message) {
        super(message);
    }
}
