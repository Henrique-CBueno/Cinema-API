package com.henrique.catalog.infra.exceptions;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message, String fieldName) {
        super(String.format(String.format(message, fieldName)));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
