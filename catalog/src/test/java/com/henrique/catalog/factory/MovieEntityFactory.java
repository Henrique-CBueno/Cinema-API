package com.henrique.catalog.factory;

import com.henrique.catalog.domain.entity.MovieEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MovieEntityFactory {

    public static MovieEntity buildMovieEntity() {
        MovieEntity entity = new MovieEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Capitao Cueca");
        entity.setDescription("filme do capitao cueca");
        entity.setDurationMinutes(90);
        entity.setRating("5 stars");
        entity.setCreatedByUserId(UUID.randomUUID());
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public static MovieEntity buildMovieEntityWithSpecificId(UUID id) {
        MovieEntity entity = buildMovieEntity();
        entity.setId(id);
        return entity;
    }

    public static MovieEntity buildMovieEntityFromRequest(String title, String description, Integer durationMinutes, String rating) {
        MovieEntity entity = new MovieEntity();
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setDurationMinutes(durationMinutes);
        entity.setRating(rating);
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }
}
