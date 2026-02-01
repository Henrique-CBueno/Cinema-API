package com.henrique.catalog.domain.dto.res.movie;

import java.util.UUID;

public record MovieResDTO(UUID id,
                          String title,
                          String description,
                          Integer durationMinutes,
                          String rating) {
}
