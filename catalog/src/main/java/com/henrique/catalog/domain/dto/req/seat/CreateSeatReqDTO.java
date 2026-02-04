package com.henrique.catalog.domain.dto.req.seat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSeatReqDTO(@NotBlank String rowLabel,

        @NotNull @Positive Integer columnNumber) {
}
