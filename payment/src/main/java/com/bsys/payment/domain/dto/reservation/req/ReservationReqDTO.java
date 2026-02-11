package com.bsys.payment.domain.dto.reservation.req;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationReqDTO (UUID reservationId,
                                 String movieTitle,
                                 BigDecimal price,
                                 String status,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime,
                                 String seat,
                                 UUID userId){
}
