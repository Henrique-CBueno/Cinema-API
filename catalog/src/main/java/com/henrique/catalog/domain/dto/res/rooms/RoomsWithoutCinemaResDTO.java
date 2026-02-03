package com.henrique.catalog.domain.dto.res.rooms;

import java.util.UUID;

public record RoomsWithoutCinemaResDTO(UUID id,
                                       String name,
                                       Integer totalRows,
                                       Integer totalColumns) {
}
