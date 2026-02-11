package com.bsys.payment.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorGlobalResponse(String error,
                                  String status,
                                  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                  LocalDateTime timestamp) {
    public ErrorGlobalResponse(String status, String error) {
        this(error,
                status,
                LocalDateTime.now());
    }
}
