package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.cinema.UpdateCinemaReqDTO;
import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.factory.CinemaFactory;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.CinemaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
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

    @Nested
    class GetCinemaById {

        @Test
        void shouldReturnCinemaDTOWhenCinemaExists() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaEntity entity = CinemaFactory.createCinemaEntity(cinemaId, "Cinemark", "São Paulo");
            CinemaResDTO dto = CinemaFactory.createCinemaResponseDTO(cinemaId, "Cinemark", "São Paulo");

            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.of(entity));
            when(cinemaMapper.toDTO(entity))
                    .thenReturn(dto);

            // Act
            CinemaResDTO result = cinemaService.getCinemaById(cinemaId);

            // Assert
            assertNotNull(result);
            assertEquals(cinemaId, result.id());
            assertEquals("Cinemark", result.name());
            assertEquals("São Paulo", result.city());
            verify(cinemaRepository, times(1)).findById(cinemaId);
            verify(cinemaMapper, times(1)).toDTO(entity);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCinemaDoesNotExist() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();

            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                cinemaService.getCinemaById(cinemaId);
            });
            verify(cinemaRepository, times(1)).findById(cinemaId);
            verify(cinemaMapper, never()).toDTO(any());
        }

        @Test
        void shouldThrowNotFoundExceptionWithCorrectMessageFormat() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();

            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                cinemaService.getCinemaById(cinemaId);
            });
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(cinemaId.toString()));
        }

        @Test
        void shouldMapEntityToDTOCorrectly() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            CinemaEntity entity = CinemaFactory.createCinemaEntity(cinemaId, "UCI Cinemas", "Rio de Janeiro");
            CinemaResDTO dto = CinemaFactory.createCinemaResponseDTO(cinemaId, "UCI Cinemas", "Rio de Janeiro");

            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.of(entity));
            when(cinemaMapper.toDTO(entity))
                    .thenReturn(dto);

            // Act
            CinemaResDTO result = cinemaService.getCinemaById(cinemaId);

            // Assert
            assertEquals(entity.getName(), result.name());
            assertEquals(entity.getCity(), result.city());
        }

        @Test
        void shouldHandleDifferentCinemaIds() {
            // Arrange
            UUID cinemaId1 = UUID.randomUUID();
            UUID cinemaId2 = UUID.randomUUID();
            CinemaEntity entity1 = CinemaFactory.createCinemaEntity(cinemaId1, "Cinema 1", "Cidade 1");
            CinemaEntity entity2 = CinemaFactory.createCinemaEntity(cinemaId2, "Cinema 2", "Cidade 2");
            CinemaResDTO dto1 = CinemaFactory.createCinemaResponseDTO(cinemaId1, "Cinema 1", "Cidade 1");
            CinemaResDTO dto2 = CinemaFactory.createCinemaResponseDTO(cinemaId2, "Cinema 2", "Cidade 2");

            when(cinemaRepository.findById(cinemaId1)).thenReturn(Optional.of(entity1));
            when(cinemaRepository.findById(cinemaId2)).thenReturn(Optional.of(entity2));
            when(cinemaMapper.toDTO(entity1)).thenReturn(dto1);
            when(cinemaMapper.toDTO(entity2)).thenReturn(dto2);

            // Act
            CinemaResDTO result1 = cinemaService.getCinemaById(cinemaId1);
            CinemaResDTO result2 = cinemaService.getCinemaById(cinemaId2);

            // Assert
            assertEquals(cinemaId1, result1.id());
            assertEquals(cinemaId2, result2.id());
            assertEquals("Cinema 1", result1.name());
            assertEquals("Cinema 2", result2.name());
        }
    }

    @Nested
    class CreateCinema {

        @Test
        void shouldCreateCinemaSuccessfully() {
            // Arrange
            UUID userId = UUID.randomUUID();
            var requestDTO = CinemaFactory.createCinemaRequestDTO();
            var entity = CinemaFactory.createCinemaEntity(requestDTO.name(), requestDTO.city());

            when(cinemaMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(cinemaRepository.saveAndFlush(any(CinemaEntity.class)))
                    .thenReturn(entity);

            // Act
            UUID result = cinemaService.createCinema(requestDTO, userId);

            // Assert
            assertNotNull(result);
            assertEquals(entity.getId(), result);
            verify(cinemaMapper, times(1)).toEntity(requestDTO);
            verify(cinemaRepository, times(1)).saveAndFlush(any(CinemaEntity.class));
        }

        @Test
        void shouldSetCreatedByUserIdWhenCreatingCinema() {
            // Arrange
            UUID userId = UUID.randomUUID();
            var requestDTO = CinemaFactory.createCinemaRequestDTO();
            var entity = CinemaFactory.createCinemaEntity(requestDTO.name(), requestDTO.city());

            when(cinemaMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(cinemaRepository.saveAndFlush(any(CinemaEntity.class)))
                    .thenReturn(entity);

            // Act
            cinemaService.createCinema(requestDTO, userId);

            // Assert
            verify(cinemaRepository, times(1)).saveAndFlush(argThat(cinema ->
                    cinema.getCreatedByUserId().equals(userId)
            ));
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenNameAndCityDuplicate() {
            // Arrange
            UUID userId = UUID.randomUUID();
            var requestDTO = CinemaFactory.createCinemaRequestDTO();
            var entity = CinemaFactory.createCinemaEntity(requestDTO.name(), requestDTO.city());

            when(cinemaMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(cinemaRepository.saveAndFlush(any(CinemaEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate"));

            // Act & Assert
            assertThrows(DuplicateResourceException.class, () -> {
                cinemaService.createCinema(requestDTO, userId);
            });
            verify(cinemaRepository, times(1)).saveAndFlush(any(CinemaEntity.class));
        }

        @Test
        void shouldMapDTOToEntityCorrectly() {
            // Arrange
            UUID userId = UUID.randomUUID();
            var requestDTO = CinemaFactory.createCinemaRequestDTO("Cinépolis", "Curitiba");
            var entity = CinemaFactory.createCinemaEntity(requestDTO.name(), requestDTO.city());

            when(cinemaMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(cinemaRepository.saveAndFlush(any(CinemaEntity.class)))
                    .thenReturn(entity);

            // Act
            cinemaService.createCinema(requestDTO, userId);

            // Assert
            verify(cinemaMapper, times(1)).toEntity(requestDTO);
        }

        @Test
        void shouldReturnUUIDOfCreatedCinema() {
            // Arrange
            UUID userId = UUID.randomUUID();
            UUID cinemaId = UUID.randomUUID();
            var requestDTO = CinemaFactory.createCinemaRequestDTO();
            var entity = CinemaFactory.createCinemaEntity(cinemaId, requestDTO.name(), requestDTO.city());

            when(cinemaMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(cinemaRepository.saveAndFlush(any(CinemaEntity.class)))
                    .thenReturn(entity);

            // Act
            UUID result = cinemaService.createCinema(requestDTO, userId);

            // Assert
            assertEquals(cinemaId, result);
        }
    }

    @Nested
    class PartialUpdate {

        @Test
        void shouldUpdateCinemaSuccessfully() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            var updateDTO = CinemaFactory.createUpdateCinemaRequestDTO("Cinépolis", "Rio de Janeiro");
            var updatedEntity = CinemaFactory.createCinemaEntity(cinemaId, "Cinépolis", "Rio de Janeiro");
            var expectedResponse = CinemaFactory.createCinemaResponseDTO(cinemaId, "Cinépolis", "Rio de Janeiro");

            when(cinemaRepository.updatePartial(cinemaId, updateDTO.name(), updateDTO.city()))
                    .thenReturn(1);
            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.of(updatedEntity));
            when(cinemaMapper.toDTO(updatedEntity))
                    .thenReturn(expectedResponse);

            // Act
            CinemaResDTO result = cinemaService.partialUpdate(cinemaId, updateDTO);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse.id(), result.id());
            assertEquals(expectedResponse.name(), result.name());
            assertEquals(expectedResponse.city(), result.city());
            verify(cinemaRepository, times(1)).updatePartial(cinemaId, updateDTO.name(), updateDTO.city());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCinemaDoesNotExistForUpdate() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            var updateDTO = CinemaFactory.createUpdateCinemaRequestDTO();

            when(cinemaRepository.updatePartial(cinemaId, updateDTO.name(), updateDTO.city()))
                    .thenReturn(0);

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> cinemaService.partialUpdate(cinemaId, updateDTO));
            verify(cinemaRepository, times(1)).updatePartial(cinemaId, updateDTO.name(), updateDTO.city());
            verify(cinemaRepository, never()).findById(any());
        }

        @Test
        void shouldThrowDuplicateResourceExceptionOnUpdateWhenNameAndCityAlreadyExist() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            var updateDTO = CinemaFactory.createUpdateCinemaRequestDTO("Cinemark", "São Paulo");

            when(cinemaRepository.updatePartial(cinemaId, updateDTO.name(), updateDTO.city()))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            // Act & Assert
            assertThrows(DuplicateResourceException.class,
                    () -> cinemaService.partialUpdate(cinemaId, updateDTO));
            verify(cinemaRepository, times(1)).updatePartial(cinemaId, updateDTO.name(), updateDTO.city());
        }

        @Test
        void shouldUpdateCinemaWithPartialData() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            var updateDTO = CinemaFactory.createUpdateCinemaRequestDTO("UCI", "Brasília");
            var updatedEntity = CinemaFactory.createCinemaEntity(cinemaId, "UCI", "Brasília");
            var expectedResponse = CinemaFactory.createCinemaResponseDTO(cinemaId, "UCI", "Brasília");

            when(cinemaRepository.updatePartial(cinemaId, updateDTO.name(), updateDTO.city()))
                    .thenReturn(1);
            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.of(updatedEntity));
            when(cinemaMapper.toDTO(updatedEntity))
                    .thenReturn(expectedResponse);

            // Act
            CinemaResDTO result = cinemaService.partialUpdate(cinemaId, updateDTO);

            // Assert
            assertEquals("UCI", result.name());
            assertEquals("Brasília", result.city());
        }

        @Test
        void shouldThrowNotFoundExceptionIfCinemaIsNotFoundAfterUpdate() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            var updateDTO = CinemaFactory.createUpdateCinemaRequestDTO();

            when(cinemaRepository.updatePartial(cinemaId, updateDTO.name(), updateDTO.city()))
                    .thenReturn(1);
            when(cinemaRepository.findById(cinemaId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class,
                    () -> cinemaService.partialUpdate(cinemaId, updateDTO));
            verify(cinemaRepository, times(1)).updatePartial(cinemaId, updateDTO.name(), updateDTO.city());
            verify(cinemaRepository, times(1)).findById(cinemaId);
        }
    }
}
