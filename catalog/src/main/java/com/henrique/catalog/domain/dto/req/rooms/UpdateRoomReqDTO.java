package com.henrique.catalog.domain.dto.req.rooms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateRoomReqDTO(@Size(min = 1, max = 255)
                               String name,

                               @Min(1)
                               Integer totalRows,

                               @Min(1)
                               Integer totalColumns) {
}
