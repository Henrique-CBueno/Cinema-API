package com.henrique.catalog.domain.dto.res.rooms;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;

import java.util.UUID;

public record RoomsResDTO (UUID id,
                           CinemaResDTO cinema,
                           String name,
                           Integer totalRows,
                           Integer totalColumns){
}
