package com.henrique.catalog.infra.exceptions;

public class MovieDontExistsException extends RuntimeException {
    public MovieDontExistsException(String message) {
        super(message);
    }
}
