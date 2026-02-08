package com.bsys.reservation.domain.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateReservationDTO(@NotNull(message = "O ID da sessão é obrigatório")
                                   UUID sessionId,


                                   @NotEmpty(message = "Selecione pelo menos um assento")
                                   List<UUID> seats) {
}
