package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

public class MovieResponseFactory {

    public static Page<MovieResDTO> buildWithOneItem() {
        MovieResDTO movieResDTO = new MovieResDTO(
                UUID.randomUUID(),
                "Capitao Cueca",
                "filme do capitao cueca",
                90,
                "5 stars"
        );

        return new PageImpl<>(List.of(movieResDTO));
    }

    public static Page<MovieResDTO> buildWithNoItem() {

        return new PageImpl<>(List.of());
    }

    public static MovieResDTO buildFindMovieById() {
        return new MovieResDTO(
                UUID.randomUUID(),
                "Capitao Cueca",
                "filme do capitao cueca",
                90,
                "5 stars"
        );
    }

    public static CreateMovieReqDTO buildCreateMovieRequest() {
        return new CreateMovieReqDTO(
                "Capitao Cueca",
                "filme do capitao cueca",
                90,
                "5 stars"
        );
    }

    public static UpdateMovieReqDTO buildUpdateMovieRequest() {
        return new UpdateMovieReqDTO(
                "Novo Titulo",
                "Nova descricao",
                120,
                "4 stars"
        );
    }

    public static UpdateMovieReqDTO buildPartialUpdateMovieRequest() {
        return new UpdateMovieReqDTO(
                "Novo Titulo",
                null,
                null,
                null
        );
    }
}
