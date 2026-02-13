package com.bsys.tickets.domain.dto;

import java.math.BigDecimal;

public record ReservationPaidConsumerDTO(CustomerDTO customer,
                                         String reservationId,
                                         String seat,
                                         String formatedDateHour,
                                         String movieName,
                                         String cinemaName,
                                         String roomName,
                                         String message,
                                         BigDecimal value) {
}
