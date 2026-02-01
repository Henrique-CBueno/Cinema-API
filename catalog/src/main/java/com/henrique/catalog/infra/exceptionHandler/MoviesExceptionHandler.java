package com.henrique.catalog.infra.exceptionHandler;


import com.henrique.catalog.infra.exceptions.MovieDontExistsException;
import com.henrique.catalog.infra.padronize.ErrorGlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class MoviesExceptionHandler {

    @ExceptionHandler(MovieDontExistsException.class)
    public ResponseEntity<ErrorGlobalResponse> movieDontExists(MovieDontExistsException ex){

        HttpStatus notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(notFound)
                .body(new ErrorGlobalResponse(
                        notFound.name(),
                        ex.getMessage()
                ));
    }
}
