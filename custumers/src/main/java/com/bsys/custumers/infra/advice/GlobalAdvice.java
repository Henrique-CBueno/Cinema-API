package com.bsys.custumers.infra.advice;

import com.bsys.custumers.infra.exceptions.NotFoundException;
import com.bsys.custumers.infra.padronize.ErrorGlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorGlobalResponse> movieDontExists(IllegalStateException ex) {

        HttpStatus conflict = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(conflict)
                .body(new ErrorGlobalResponse(
                        conflict.name(),
                        ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorGlobalResponse> movieDontExists(NotFoundException ex) {

        HttpStatus notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(notFound)
                .body(new ErrorGlobalResponse(
                        notFound.name(),
                        ex.getMessage()));
    }
}
