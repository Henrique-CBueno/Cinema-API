package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.factory.MovieEntityFactory;
import com.henrique.catalog.factory.MovieResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovieMapperTest {

    //INJETO ASSIM POIS É UMA INTERFACE
    private MovieMapper movieMapper;

    @BeforeEach
    void setUp() {
        movieMapper = Mappers.getMapper(MovieMapper.class);
    }

    @Nested
    class toResponse {

        @Test
        void shouldMapEntityToResponseDTO() {

            // ARRANGE
            MovieEntity entity = MovieEntityFactory.buildMovieEntity();

            // ACT
            MovieResDTO response = movieMapper.toResponse(entity);

            // ASSERT
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getTitle(), response.title());
            assertEquals(entity.getDescription(), response.description());
            assertEquals(entity.getDurationMinutes(), response.durationMinutes());
            assertEquals(entity.getRating(), response.rating());
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {

            // ARRANGE
            MovieEntity entity = null;

            // ACT
            MovieResDTO response = movieMapper.toResponse(entity);

            // ASSERT
            assertNull(response);
        }

        @Test
        void shouldMapAllFieldsCorrectly() {

            // ARRANGE
            UUID specificId = UUID.randomUUID();
            MovieEntity entity = MovieEntityFactory.buildMovieEntityWithSpecificId(specificId);
            entity.setTitle("Novo Filme");
            entity.setDescription("Descricao nova");
            entity.setDurationMinutes(120);
            entity.setRating("4 stars");

            // ACT
            MovieResDTO response = movieMapper.toResponse(entity);

            // ASSERT
            assertEquals(specificId, response.id());
            assertEquals("Novo Filme", response.title());
            assertEquals("Descricao nova", response.description());
            assertEquals(120, response.durationMinutes());
            assertEquals("4 stars", response.rating());
        }
    }

    @Nested
    class toEntity {

        @Test
        void shouldMapCreateDTOToEntity() {

            // ARRANGE
            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();

            // ACT
            MovieEntity entity = movieMapper.toEntity(dto);

            // ASSERT
            assertNotNull(entity);
            assertEquals(dto.title(), entity.getTitle());
            assertEquals(dto.description(), entity.getDescription());
            assertEquals(dto.durationMinutes(), entity.getDurationMinutes());
            assertEquals(dto.rating(), entity.getRating());
        }

        @Test
        void shouldReturnNullWhenDTOIsNull() {

            // ARRANGE
            CreateMovieReqDTO dto = null;

            // ACT
            MovieEntity entity = movieMapper.toEntity(dto);

            // ASSERT
            assertNull(entity);
        }

        @Test
        void shouldMapAllFieldsFromDTOCorrectly() {

            // ARRANGE
            CreateMovieReqDTO dto = new CreateMovieReqDTO(
                    "Filme Teste",
                    "Descricao do filme teste",
                    150,
                    "3 stars"
            );

            // ACT
            MovieEntity entity = movieMapper.toEntity(dto);

            // ASSERT
            assertEquals("Filme Teste", entity.getTitle());
            assertEquals("Descricao do filme teste", entity.getDescription());
            assertEquals(150, entity.getDurationMinutes());
            assertEquals("3 stars", entity.getRating());
        }

        @Test
        void shouldNotMapIdWhenCreatingFromDTO() {

            // ARRANGE
            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();

            // ACT
            MovieEntity entity = movieMapper.toEntity(dto);

            // ASSERT
            assertNull(entity.getId());
        }

        @Test
        void shouldNotMapCreatedByUserIdWhenCreatingFromDTO() {

            // ARRANGE
            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();

            // ACT
            MovieEntity entity = movieMapper.toEntity(dto);

            // ASSERT
            assertNull(entity.getCreatedByUserId());
        }
    }
}