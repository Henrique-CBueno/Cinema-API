package com.bsys.reservation.publisher.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ReservationPaidPublish(CustomerDTO customer,
                                     String reservationId,
                                     String seat,
                                     String formatedDateHour,
                                     String movieName,
                                     String cinemaName,
                                     String roomName,
                                     String message,
                                     BigDecimal value) {
}




//id da reserva, assento, hora,  nome filme, nome do cinema, npme da sala, valor da reserva
// customer --nome --taxId --telefone --email,