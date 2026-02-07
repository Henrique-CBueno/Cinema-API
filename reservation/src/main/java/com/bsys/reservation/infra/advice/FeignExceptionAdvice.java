package com.bsys.reservation.infra.advice;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeignExceptionAdvice {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<FeignErrorResponse> handleFeignException(FeignException ex) {
        HttpStatus status = resolveStatus(ex);
        String message = ex.contentUTF8();
        if (message == null || message.isBlank()) {
            message = ex.getMessage();
        }

        return ResponseEntity.status(status)
                .body(new FeignErrorResponse(status.name(), message));
    }

    private HttpStatus resolveStatus(FeignException ex) {
        int status = ex.status();
        if (status <= 0) {
            return HttpStatus.BAD_GATEWAY;
        }

        HttpStatus resolved = HttpStatus.resolve(status);
        return resolved != null ? resolved : HttpStatus.BAD_GATEWAY;
    }

    public record FeignErrorResponse(String error, String message) {
    }
}
