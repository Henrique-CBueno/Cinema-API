package com.bsys.reservation.clients.catalog.DTO;


import java.util.UUID;

public record RoomsResDTO(UUID id,
                          CinemaResDTO cinema,
                          String name,
                          Integer totalRows,
                          Integer totalColumns) {
}
