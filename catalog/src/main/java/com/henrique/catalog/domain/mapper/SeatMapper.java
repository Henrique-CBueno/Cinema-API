package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.entity.SeatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "room.id", target = "roomId")
    SeatResDTO toDTO(SeatEntity seatEntity);

    SeatEntity toEntity(CreateSeatReqDTO createSeatReqDTO);
}
