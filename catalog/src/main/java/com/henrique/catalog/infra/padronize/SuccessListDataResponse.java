package com.henrique.catalog.infra.padronize;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record SuccessListDataResponse(List<?> data,
                                      Long page,
                                      Long pageSize,
                                      Long totalElements,
                                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime timestamp) {
    public SuccessListDataResponse(List<?> data, Long page, Long pageSize, Long totalElements) {
        this(data,
                page,
                pageSize,
                totalElements,
                LocalDateTime.now());
    }
}
