package com.henrique.catalog.domain.dto.res.batch;

import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;

import java.util.UUID;

public record BatchResDTO(UUID reservationId,
                          SessionResDTO sessionResDTO,
                          SeatResDTO seatResDTO) {
}
