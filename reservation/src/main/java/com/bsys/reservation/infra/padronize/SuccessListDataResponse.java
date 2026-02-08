package com.bsys.reservation.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record SuccessListDataResponse<T>(List<T> data,
                                      Integer page,
                                      Integer pageSize,
                                      Long totalElements,
                                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime timestamp) {
    public SuccessListDataResponse(List<T> data, Integer page, Integer pageSize, Long totalElements) {
        this( data,
                page,
                pageSize,
                totalElements,
                LocalDateTime.now());
    }
}
