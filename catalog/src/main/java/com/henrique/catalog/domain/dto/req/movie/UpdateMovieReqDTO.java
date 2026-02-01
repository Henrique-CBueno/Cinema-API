package com.henrique.catalog.domain.dto.req.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateMovieReqDTO(@Size(min = 1, max = 255)
                                String title,

                                @Size(max = 1000)
                                String description,

                                @Min(1)
                                Integer durationMinutes,

                                String rating) {
}
