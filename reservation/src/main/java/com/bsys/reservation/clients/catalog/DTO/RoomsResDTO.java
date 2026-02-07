package com.bsys.reservation.clients.catalog.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RoomsResDTO(UUID id,
                          MovieResDTO movie,
                          RoomsResDTO room,
                          LocalDateTime startTime,
                          LocalDateTime endTime,
                          BigDecimal price,
                          SessionStatus status) {
}
