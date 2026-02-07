package com.bsys.reservation.infra.exceptions;

public class SeatDontExistsException extends RuntimeException {
    public SeatDontExistsException(String message) {
        super(message);
    }
}
