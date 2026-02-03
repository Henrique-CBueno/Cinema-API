package com.henrique.catalog.domain.dto.req.cinema;

import jakarta.validation.constraints.NotBlank;

public record CreateCinemaReqDTO(@NotBlank(message = "O nome é obrigatório")
                                 String name,

                                 @NotBlank(message = "A cidade é obrigatória")
                                 String city) {
}
