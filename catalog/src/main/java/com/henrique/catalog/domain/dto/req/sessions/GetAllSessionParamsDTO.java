package com.henrique.catalog.domain.dto.req.sessions;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record GetAllSessionParamsDTO(UUID movieId,
                                     UUID cinemaId,
                                     UUID roomId,
                                     LocalDate date) {

    public String formatData() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date);
    }
}
