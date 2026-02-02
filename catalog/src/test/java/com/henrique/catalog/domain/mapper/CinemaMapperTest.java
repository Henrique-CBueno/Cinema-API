package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.factory.CinemaFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CinemaMapperTest {

    private CinemaMapper cinemaMapper;

    @BeforeEach
    void setUp() {
        cinemaMapper = Mappers.getMapper(CinemaMapper.class);
    }

    @Nested
    class ToDTO {

        @Test
        void shouldMapEntityToResponseDTO() {
            // Arrange
            CinemaEntity entity = CinemaFactory.createCinemaEntity();

            // Act
            CinemaResDTO response = cinemaMapper.toDTO(entity);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getName(), response.name());
            assertEquals(entity.getCity(), response.city());
        }

        @Test
        void shouldReturnNullWhenEntityIsNull() {
            // Arrange
            CinemaEntity entity = null;

            // Act
            CinemaResDTO response = cinemaMapper.toDTO(entity);

            // Assert
            assertNull(response);
        }

        @Test
        void shouldMapAllFieldsCorrectly() {
            // Arrange
            CinemaEntity entity = CinemaFactory.createCinemaEntity("UCI Cinemas", "Rio de Janeiro");

            // Act
            CinemaResDTO response = cinemaMapper.toDTO(entity);

            // Assert
            assertEquals("UCI Cinemas", response.name());
            assertEquals("Rio de Janeiro", response.city());
        }

        @Test
        void shouldMapDifferentCinemaEntities() {
            // Arrange
            CinemaEntity cinema1 = CinemaFactory.createCinemaEntity("Kinoplex", "Brasília");
            CinemaEntity cinema2 = CinemaFactory.createCinemaEntity("Cine Araújo", "Belo Horizonte");

            // Act
            CinemaResDTO response1 = cinemaMapper.toDTO(cinema1);
            CinemaResDTO response2 = cinemaMapper.toDTO(cinema2);

            // Assert
            assertEquals("Kinoplex", response1.name());
            assertEquals("Brasília", response1.city());
            assertEquals("Cine Araújo", response2.name());
            assertEquals("Belo Horizonte", response2.city());
        }

        @Test
        void shouldNotMapIdToDTO() {
            // Arrange
            UUID specificId = UUID.randomUUID();
            CinemaEntity entity = CinemaFactory.createCinemaEntity(specificId, "Cinépolis", "Curitiba");

            // Act
            CinemaResDTO response = cinemaMapper.toDTO(entity);

            // Assert
            assertNotNull(response);
            assertEquals("Cinépolis", response.name());
            assertEquals("Curitiba", response.city());
            // O DTO não tem campo ID, confirmando que não é mapeado
        }
    }
}