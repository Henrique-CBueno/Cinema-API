package com.henrique.catalog.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorGlobalResponse(String error,
                                  String message,
                                  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                  LocalDateTime timestamp) {
    public ErrorGlobalResponse(String message, String error) {
        this(error,
                message,
                LocalDateTime.now());
    }
}
