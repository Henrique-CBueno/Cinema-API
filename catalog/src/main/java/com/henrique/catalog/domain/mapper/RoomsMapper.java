package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CinemaMapper.class})
public interface RoomsMapper {

    @Mapping(source = "cinema", target = "cinemaResDTO")
    RoomsResDTO toDTO(RoomEntity room);
}
