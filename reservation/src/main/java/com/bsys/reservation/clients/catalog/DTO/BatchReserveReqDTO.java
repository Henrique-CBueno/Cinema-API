package com.bsys.reservation.clients.catalog.DTO;

import java.util.UUID;

public record BatchReserveReqDTO(UUID reserveId,
                                 UUID sessionId,
                                 UUID seatId) {
}
