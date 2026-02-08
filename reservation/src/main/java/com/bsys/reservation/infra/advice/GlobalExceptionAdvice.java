package com.bsys.reservation.infra.advice;

import com.bsys.reservation.infra.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(SeatDontExistsException.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> naoExisteAssento(SeatDontExistsException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT.toString(), ex.getMessage()));
    }

    @ExceptionHandler(SeatUnavailableException.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> assentoIndisponivel(SeatUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }

    @ExceptionHandler(ReservationPersistenceException.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> erroAoCriarReserva(ReservationPersistenceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }

    @ExceptionHandler(SessionUnavailableException.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> sessaoIndisponivel(SessionUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }

    @ExceptionHandler(SeatAlreadyReservedException.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> assentoJaReservado(SeatAlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }

    @ExceptionHandler(ReservationsNotFound.class)
    public ResponseEntity<FeignExceptionAdvice.ErrorResponse> reservasNaoExistem(ReservationsNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new FeignExceptionAdvice.ErrorResponse(HttpStatus.CONFLICT.toString(), ex.getMessage()));
    }
}
