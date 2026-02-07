package com.henrique.catalog.domain.dto.res.seat;

import java.util.List;
import java.util.UUID;

public record SeatsExistenceResDTO(boolean allExists, List<UUID> missingSeatIds) {
}
