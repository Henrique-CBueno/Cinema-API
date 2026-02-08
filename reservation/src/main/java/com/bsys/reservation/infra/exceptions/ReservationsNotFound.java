package com.bsys.reservation.infra.exceptions;

public class ReservationsNotFound extends RuntimeException {
    public ReservationsNotFound(String message) {
        super(message);
    }
}
