package com.henrique.catalog.domain.dto.res.session;

import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SessionResDTO(UUID id,
        MovieResDTO movie,
        RoomsResDTO room,
        UUID cinemaId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal price,
        SessionStatus status) {
}
