package com.henrique.catalog.domain.dto.res.cinema;

import java.util.UUID;

public record CinemaResDTO(UUID id,
                           String name,
                           String city) {
}
