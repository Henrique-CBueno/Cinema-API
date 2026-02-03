package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsWithoutCinemaResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomsMapper {

    RoomsWithoutCinemaResDTO toDTO(RoomEntity roomEntity);
}
