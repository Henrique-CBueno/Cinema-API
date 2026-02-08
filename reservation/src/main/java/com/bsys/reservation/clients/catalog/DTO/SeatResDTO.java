package com.bsys.reservation.clients.catalog.DTO;

import java.util.UUID;

public record SeatResDTO(UUID id,
                         UUID roomId,
                         String rowLabel,
                         String columnNumber) {
}
