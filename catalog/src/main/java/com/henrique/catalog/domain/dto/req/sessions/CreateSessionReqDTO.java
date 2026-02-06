package com.henrique.catalog.domain.dto.req.sessions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateSessionReqDTO(@NotNull UUID movieId,

                                    @NotNull UUID roomId,

                                    @NotNull UUID cinemaId,

                                    @NotNull LocalDateTime startTime,

                                    @NotNull @Min(1) BigDecimal price) {
}
