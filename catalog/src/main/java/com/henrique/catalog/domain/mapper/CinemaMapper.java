package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    CinemaResDTO toDTO(CinemaEntity cinemaEntity);
}
