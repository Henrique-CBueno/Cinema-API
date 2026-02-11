package com.bsys.reservation.domain.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservationResDTO(UUID reservationId,
                String movieTitle,
                BigDecimal price,
                String status,
                LocalDateTime startTime,
                LocalDateTime endTime,
                List<String> seats,
                UUID userId
) {
}
