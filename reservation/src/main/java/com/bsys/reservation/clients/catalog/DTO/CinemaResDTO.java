package com.bsys.reservation.clients.catalog.DTO;

import java.util.UUID;

public record CinemaResDTO(UUID id,
                           String name,
                           String city) {
}
