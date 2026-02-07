package com.bsys.reservation.clients.catalog.DTO;

import java.util.UUID;

public record MovieResDTO(UUID id,
                          String title,
                          String description,
                          Integer durationMinutes,
                          String rating) {
}
