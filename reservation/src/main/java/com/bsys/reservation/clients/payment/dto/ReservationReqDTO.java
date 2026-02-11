package com.bsys.reservation.clients.payment.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservationReqDTO(UUID reservationId,
                                String movieTitle,
                                BigDecimal price,
                                String status,
                                LocalDateTime startTime,
                                LocalDateTime endTime,
                                List<String> seats,
                                UUID userId) {
}