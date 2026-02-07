package com.bsys.reservation.clients.catalog.DTO;

import java.util.List;
import java.util.UUID;

public record SeatsExistenceResDTO(boolean allExists, List<UUID> missingSeatIds) {
}
