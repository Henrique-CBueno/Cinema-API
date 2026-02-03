package com.henrique.catalog.domain.dto.res.rooms;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import jakarta.persistence.Column;

import java.util.UUID;

public record RoomsResDTO (UUID id,
                           CinemaResDTO cinemaResDTO,
                           String name,
                           Integer totalRows,
                           Integer totalColumns){
}
