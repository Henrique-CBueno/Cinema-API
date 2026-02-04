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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RoomEntity toEntity(CreateRoomReqDTO dto);
}
