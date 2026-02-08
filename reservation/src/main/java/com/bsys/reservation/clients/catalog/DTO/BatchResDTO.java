package com.bsys.reservation.clients.catalog.DTO;

import java.util.UUID;

public record BatchResDTO(UUID reservationId,
                          SessionResDTO sessionResDTO,
                          SeatResDTO seatResDTO) {
}
