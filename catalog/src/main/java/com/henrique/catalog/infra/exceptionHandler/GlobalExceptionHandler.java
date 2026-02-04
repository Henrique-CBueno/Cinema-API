package com.henrique.catalog.infra.exceptionHandler;

import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.infra.exceptions.UnprocessableEntityException;
import com.henrique.catalog.infra.padronize.ErrorGlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorGlobalResponse> movieDontExists(NotFoundException ex) {

                HttpStatus notFound = HttpStatus.NOT_FOUND;
                return ResponseEntity
                                .status(notFound)
                                .body(new ErrorGlobalResponse(
                                                notFound.name(),
                                                ex.getMessage()));
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorGlobalResponse> duplicateResource(DuplicateResourceException ex) {

                HttpStatus conflict = HttpStatus.CONFLICT;
                return ResponseEntity
                                .status(conflict)
                                .body(new ErrorGlobalResponse(
                                                conflict.name(),
                                                ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorGlobalResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

                HttpStatus unprocessableContent = HttpStatus.UNPROCESSABLE_CONTENT;
                return ResponseEntity
                                .status(unprocessableContent)
                                .body(new ErrorGlobalResponse(
                                                unprocessableContent.name(),
                                                ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage()));
        }

        @ExceptionHandler(UnprocessableEntityException.class)
        public ResponseEntity<ErrorGlobalResponse> handleUnprocessableEntity(UnprocessableEntityException ex) {

                HttpStatus unprocessableContent = HttpStatus.UNPROCESSABLE_CONTENT;
                return ResponseEntity
                                .status(unprocessableContent)
                                .body(new ErrorGlobalResponse(
                                                unprocessableContent.name(),
                                                ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorGlobalResponse> handleTypeMismatch() {

                HttpStatus badRequest = HttpStatus.BAD_REQUEST;
                return ResponseEntity
                        .status(badRequest)
                        .body(new ErrorGlobalResponse(badRequest.name(), "BODY NAO COMPATIVEL"));
        }
}
