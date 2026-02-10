package com.bsys.custumers.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SuccessResponse<T>(T data,
                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                              LocalDateTime timestamp) {
    public SuccessResponse(T data) {
        this(data, LocalDateTime.now());
    }
}
