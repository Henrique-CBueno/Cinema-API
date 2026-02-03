package com.henrique.catalog.domain.dto.req.rooms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomReqDTO (@NotBlank
                                String name,

                                @NotNull @Min(1)
                                Integer totalRows,

                                @NotNull @Min(1)
                                Integer totalColumns) {
}
