package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.cinema.CreateCinemaReqDTO;
import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    CinemaResDTO toDTO(CinemaEntity cinemaEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CinemaEntity toEntity(CreateCinemaReqDTO createCinemaReqDTO);
}
