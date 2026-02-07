package com.bsys.reservation.infra.exceptions;

public class SessionUnavailableException extends RuntimeException {
    public SessionUnavailableException(String message) {
        super(message);
    }
}
