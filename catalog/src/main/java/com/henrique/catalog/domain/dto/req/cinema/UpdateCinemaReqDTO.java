package com.henrique.catalog.domain.dto.req.cinema;

import jakarta.validation.constraints.Size;

public record UpdateCinemaReqDTO(@Size(min = 1, max = 255)
                                 String name,

                                 @Size(min = 1, max = 255)
                                 String city) {
}
