package com.henrique.catalog.domain.dto.req.batch;

import java.util.UUID;

public record BatchReserveReqDTO(UUID reserveId,
                                 UUID sessionId,
                                 UUID seatId,
                                 UUID userId) {
}
