package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.rooms.CreateRoomReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CinemaMapper.class})
public interface RoomsMapper {

    @Mapping(source = "cinema", target = "cinema")
    RoomsResDTO toDTO(RoomEntity room);

    RoomEntity toEntity(CreateRoomReqDTO dto);
}
