package com.henrique.catalog.domain.dto.res.seat;

import java.util.UUID;

public record SeatResDTO(UUID id,
                         UUID roomId,
                         String rowLabel,
                         String columnNumber) {
}
