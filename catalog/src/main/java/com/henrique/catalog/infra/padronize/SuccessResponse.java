package com.henrique.catalog.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SuccessResponse(Object data,
                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                              LocalDateTime timestamp) {
    public SuccessResponse(Object data) {
        this(data, LocalDateTime.now());
    }
}
