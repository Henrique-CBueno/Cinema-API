package com.bsys.reservation.infra.exceptions;

public class ReservationPersistenceException extends RuntimeException {
    public ReservationPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
