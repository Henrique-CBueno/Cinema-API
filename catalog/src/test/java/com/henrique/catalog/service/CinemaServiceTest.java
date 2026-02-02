package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.factory.CinemaFactory;
import com.henrique.catalog.repository.CinemaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CinemaServiceTest {

    @Mock
    private CinemaRepository cinemaRepository;

    @Mock
    private CinemaMapper cinemaMapper;

    @InjectMocks
    private CinemaService cinemaService;

    @Nested
    class GetAllCinemas {

        @Test
        void shouldReturnPageOfCinemasWhenCinemasExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            CinemaEntity entity = CinemaFactory.createCinemaEntity();
            CinemaResDTO dto = CinemaFactory.createCinemaResponseDTO();
            Page<CinemaEntity> entityPage = new PageImpl<>(List.of(entity));

            when(cinemaRepository.findAll(pageable))
                    .thenReturn(entityPage);
            when(cinemaMapper.toDTO(entity))
                    .thenReturn(dto);

            // Act
            Page<CinemaResDTO> result = cinemaService.getAllCinemas(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(dto.name(), result.getContent().get(0).name());
            verify(cinemaRepository, times(1)).findAll(pageable);
            verify(cinemaMapper, times(1)).toDTO(entity);
        }

        @Test
        void shouldReturnEmptyPageWhenNoCinemasExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            Page<CinemaEntity> emptyPage = new PageImpl<>(List.of());

            when(cinemaRepository.findAll(pageable))
                    .thenReturn(emptyPage);

            // Act
            Page<CinemaResDTO> result = cinemaService.getAllCinemas(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.isEmpty());
            verify(cinemaRepository, times(1)).findAll(pageable);
        }

        @Test
        void shouldReturnMultipleCinemasWhenPaginationIsUsed() {
            // Arrange
            Pageable pageable = Pageable.ofSize(2);
            CinemaEntity entity1 = CinemaFactory.createCinemaEntity("Cinema 1", "São Paulo");
            CinemaEntity entity2 = CinemaFactory.createCinemaEntity("Cinema 2", "Rio de Janeiro");
            CinemaResDTO dto1 = CinemaFactory.createCinemaResponseDTO("Cinema 1", "São Paulo");
            CinemaResDTO dto2 = CinemaFactory.createCinemaResponseDTO("Cinema 2", "Rio de Janeiro");
            Page<CinemaEntity> entityPage = new PageImpl<>(List.of(entity1, entity2));

            when(cinemaRepository.findAll(pageable))
                    .thenReturn(entityPage);
            when(cinemaMapper.toDTO(entity1))
                    .thenReturn(dto1);
            when(cinemaMapper.toDTO(entity2))
                    .thenReturn(dto2);

            // Act
            Page<CinemaResDTO> result = cinemaService.getAllCinemas(pageable);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            verify(cinemaRepository, times(1)).findAll(pageable);
            verify(cinemaMapper, times(2)).toDTO(any());
        }

        @Test
        void shouldMapAllEntitiesCorrectly() {
            // Arrange
            Pageable pageable = Pageable.ofSize(3);
            CinemaEntity entity1 = CinemaFactory.createCinemaEntity("Cinemark", "Brasília");
            CinemaEntity entity2 = CinemaFactory.createCinemaEntity("UCI", "Curitiba");
            CinemaEntity entity3 = CinemaFactory.createCinemaEntity("Kinoplex", "Fortaleza");

            CinemaResDTO dto1 = CinemaFactory.createCinemaResponseDTO("Cinemark", "Brasília");
            CinemaResDTO dto2 = CinemaFactory.createCinemaResponseDTO("UCI", "Curitiba");
            CinemaResDTO dto3 = CinemaFactory.createCinemaResponseDTO("Kinoplex", "Fortaleza");

            Page<CinemaEntity> entityPage = new PageImpl<>(List.of(entity1, entity2, entity3));

            when(cinemaRepository.findAll(pageable))
                    .thenReturn(entityPage);
            when(cinemaMapper.toDTO(entity1))
                    .thenReturn(dto1);
            when(cinemaMapper.toDTO(entity2))
                    .thenReturn(dto2);
            when(cinemaMapper.toDTO(entity3))
                    .thenReturn(dto3);

            // Act
            Page<CinemaResDTO> result = cinemaService.getAllCinemas(pageable);

            // Assert
            assertEquals(3, result.getTotalElements());
            assertEquals("Cinemark", result.getContent().get(0).name());
            assertEquals("UCI", result.getContent().get(1).name());
            assertEquals("Kinoplex", result.getContent().get(2).name());
        }

        @Test
        void shouldRespectPageableParameters() {
            // Arrange
            Pageable pageable = Pageable.ofSize(5).withPage(1);
            Page<CinemaEntity> entityPage = new PageImpl<>(List.of(), pageable, 0);

            when(cinemaRepository.findAll(pageable))
                    .thenReturn(entityPage);

            // Act
            Page<CinemaResDTO> result = cinemaService.getAllCinemas(pageable);

            // Assert
            assertEquals(1, result.getNumber());
            assertEquals(5, result.getSize());
            verify(cinemaRepository, times(1)).findAll(pageable);
        }
    }
}
