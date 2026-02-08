package com.bsys.reservation.domain.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResDTO(UUID reservationId,
                                String movieTitle,
                                String status,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                String seat,
                                UUID userId) {
}
