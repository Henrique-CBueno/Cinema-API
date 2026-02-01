package com.henrique.catalog.domain.dto.req.movie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMovieReqDTO(@NotBlank(message = "O título é obrigatório")
                                String title,

                                @NotBlank(message = "A descrição é obrigatória")
                                String description,

                                @NotNull(message = "A duração é obrigatória")
                                @Min(value = 1, message = "A duração deve ser de pelo menos 1 minuto")
                                Integer durationMinutes,

                                @NotBlank(message = "A classificação é obrigatória")
                                String rating) {
}
