package com.henrique.catalog.factory;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MovieFactory {

    private static final String DEFAULT_TITLE = "The Matrix";
    private static final String DEFAULT_DESCRIPTION = "A computer hacker learns about the true nature of the reality he lives in.";
    private static final Integer DEFAULT_DURATION = 136;
    private static final String DEFAULT_RATING = "R";
    private static final UUID DEFAULT_USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    public static MovieEntity createMovieEntity() {
        return createMovieEntity(UUID.randomUUID(), DEFAULT_TITLE);
    }

    public static MovieEntity createMovieEntity(UUID id, String title) {
        MovieEntity movie = new MovieEntity();
        movie.setId(id);
        movie.setTitle(title);
        movie.setDescription(DEFAULT_DESCRIPTION);
        movie.setDurationMinutes(DEFAULT_DURATION);
        movie.setRating(DEFAULT_RATING);
        movie.setCreatedByUserId(DEFAULT_USER_ID);
        movie.setActive(true);
        movie.setCreatedAt(LocalDateTime.now());
        return movie;
    }

    public static MovieEntity createInactiveMovieEntity(UUID id) {
        MovieEntity movie = createMovieEntity(id, DEFAULT_TITLE);
        movie.setActive(false);
        return movie;
    }

    public static CreateMovieReqDTO createMovieRequestDTO() {
        return new CreateMovieReqDTO(
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_DURATION,
                DEFAULT_RATING
        );
    }

    public static CreateMovieReqDTO createMovieRequestDTO(String title, String description) {
        return new CreateMovieReqDTO(
                title,
                description,
                DEFAULT_DURATION,
                DEFAULT_RATING
        );
    }

    public static UpdateMovieReqDTO createUpdateMovieRequestDTO() {
        return new UpdateMovieReqDTO(
                DEFAULT_TITLE + " Updated",
                DEFAULT_DESCRIPTION + " Updated",
                150,
                "PG-13"
        );
    }

    public static UpdateMovieReqDTO createUpdateMovieRequestDTO(String title, String description) {
        return new UpdateMovieReqDTO(
                title,
                description,
                150,
                "PG-13"
        );
    }

    public static UpdateMovieReqDTO createPartialUpdateMovieRequestDTO(String title) {
        return new UpdateMovieReqDTO(
                title,
                null,
                null,
                null
        );
    }

    public static MovieResDTO createMovieResponseDTO() {
        return new MovieResDTO(
                UUID.randomUUID(),
                DEFAULT_TITLE,
                DEFAULT_DESCRIPTION,
                DEFAULT_DURATION,
                DEFAULT_RATING
        );
    }

    public static MovieResDTO createMovieResponseDTO(UUID id, String title) {
        return new MovieResDTO(
                id,
                title,
                DEFAULT_DESCRIPTION,
                DEFAULT_DURATION,
                DEFAULT_RATING
        );
    }
}
