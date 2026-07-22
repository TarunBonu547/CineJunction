package com.cinejunction.userlist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ListMovieNotFoundException extends RuntimeException {
    public ListMovieNotFoundException(String message) {
        super(message);
    }
}
