package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResDTO toResponse(MovieEntity movieEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MovieEntity toEntity(CreateMovieReqDTO dto);
}
