package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResDTO toResponse(MovieEntity movieEntity);

    MovieEntity toEntity(CreateMovieReqDTO dto);
}
