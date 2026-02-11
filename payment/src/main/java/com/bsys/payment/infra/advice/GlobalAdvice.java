package com.bsys.payment.infra.advice;

import com.bsys.payment.infra.padronize.ErrorGlobalResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalAdvice {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorGlobalResponse> handleFeignException(FeignException e) {
        if (e.status() == 404) {
            String content = e.contentUTF8();
            try {
                // Try to parse as list as per user's log
                List<Map<String, String>> errors = objectMapper.readValue(content, new TypeReference<>() {
                });
                if (!errors.isEmpty()) {
                    Map<String, String> error = errors.getFirst();
                    String message = error.getOrDefault("error", "Error requesting from customer service");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorGlobalResponse(HttpStatus.NOT_FOUND.toString(), message));
                }
            } catch (JsonProcessingException parseException) {
                // Fallback if parsing fails or structure is different
                try {
                    // Try parsing as single object just in case
                    Map<String, String> error = objectMapper.readValue(content, new TypeReference<>() {
                    });
                    String message = error.getOrDefault("error", "Error requesting from customer service");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorGlobalResponse(HttpStatus.NOT_FOUND.toString(), message));
                } catch (JsonProcessingException ignored) {
                }
            }
            // Fallback generic 404 message if everything fails but status was 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorGlobalResponse(HttpStatus.NOT_FOUND.toString(),
                            "Resource not found in external service"));
        }

        // For other feign exceptions, rethrow or handle generically
        // If we want to return the same format:
        return ResponseEntity.status(e.status() > 0 ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(new ErrorGlobalResponse(HttpStatus.valueOf(e.status() > 0 ? e.status() : 500).toString(),
                        e.getMessage()));
    }
}
