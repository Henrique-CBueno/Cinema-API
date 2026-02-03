package com.henrique.catalog.domain.mapper;

import com.henrique.catalog.domain.dto.req.cinema.CreateCinemaReqDTO;
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
            assertEquals(entity.getId(), response.id());
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
            assertNotNull(response.id());
            assertEquals(entity.getId(), response.id());
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
            assertEquals(cinema1.getId(), response1.id());
            assertEquals("Kinoplex", response1.name());
            assertEquals("Brasília", response1.city());
            assertEquals(cinema2.getId(), response2.id());
            assertEquals("Cine Araújo", response2.name());
            assertEquals("Belo Horizonte", response2.city());
        }

        @Test
        void shouldMapIdToDTO() {
            // Arrange
            UUID specificId = UUID.randomUUID();
            CinemaEntity entity = CinemaFactory.createCinemaEntity(specificId, "Cinépolis", "Curitiba");

            // Act
            CinemaResDTO response = cinemaMapper.toDTO(entity);

            // Assert
            assertNotNull(response);
            assertEquals(specificId, response.id());
            assertEquals("Cinépolis", response.name());
            assertEquals("Curitiba", response.city());
        }
    }

    @Nested
    class toEntity {

        @Test
        void shouldMapCreateDTOToEntity() {
            // Arrange
            CreateCinemaReqDTO dto = CinemaFactory.createCinemaRequestDTO("Cine Belas Artes", "São Paulo");

            // Act
            CinemaEntity entity = cinemaMapper.toEntity(dto);

            // Assert
            assertNotNull(entity);
            assertEquals(dto.name(), entity.getName());
            assertEquals(dto.city(), entity.getCity());
        }

        @Test
        void shouldMapAllFieldsCorrectly() {
            // Arrange
            CreateCinemaReqDTO dto = CinemaFactory.createCinemaRequestDTO("UCI Cinemas", "Rio de Janeiro");

            // Act
            CinemaEntity entity = cinemaMapper.toEntity(dto);

            // Assert
            assertNotNull(entity);
            assertEquals("UCI Cinemas", entity.getName());
            assertEquals("Rio de Janeiro", entity.getCity());
        }

        @Test
        void shouldMapDifferentCinemaDTOs() {
            // Arrange
            CreateCinemaReqDTO dto1 = CinemaFactory.createCinemaRequestDTO("Kinoplex", "Brasília");
            CreateCinemaReqDTO dto2 = CinemaFactory.createCinemaRequestDTO("Cine Araújo", "Belo Horizonte");

            // Act
            CinemaEntity entity1 = cinemaMapper.toEntity(dto1);
            CinemaEntity entity2 = cinemaMapper.toEntity(dto2);

            // Assert
            assertEquals("Kinoplex", entity1.getName());
            assertEquals("Brasília", entity1.getCity());
            assertEquals("Cine Araújo", entity2.getName());
            assertEquals("Belo Horizonte", entity2.getCity());
        }

        @Test
        void shouldReturnNullWhenDTOIsNull() {
            // Arrange
            CreateCinemaReqDTO dto = null;

            // Act
            CinemaEntity entity = cinemaMapper.toEntity(dto);

            // Assert
            assertNull(entity);
        }

        @Test
        void shouldIgnoreGeneratedFields() {
            // Arrange
            CreateCinemaReqDTO dto = CinemaFactory.createCinemaRequestDTO("Cinépolis", "Curitiba");

            // Act
            CinemaEntity entity = cinemaMapper.toEntity(dto);

            // Assert
            assertNull(entity.getId());
            assertNull(entity.getCreatedByUserId());
        }

    }
}