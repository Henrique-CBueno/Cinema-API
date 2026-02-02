package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.entity.MovieEntity;
import com.henrique.catalog.domain.mapper.MovieMapper;
import com.henrique.catalog.factory.MovieFactory;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.MovieRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    @Nested
    class GetAllMovies {

        @Test
        void shouldReturnPageOfMoviesWhenMoviesExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            MovieEntity entity = MovieFactory.createMovieEntity();
            MovieResDTO dto = MovieFactory.createMovieResponseDTO();
            Page<MovieEntity> entityPage = new PageImpl<>(List.of(entity));
            
            when(movieRepository.findAll(pageable))
                    .thenReturn(entityPage);
            when(movieMapper.toResponse(entity))
                    .thenReturn(dto);

            // Act
            Page<MovieResDTO> result = movieService.getAllMovies(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(dto.title(), result.getContent().get(0).title());
            verify(movieRepository, times(1)).findAll(pageable);
            verify(movieMapper, times(1)).toResponse(entity);
        }

        @Test
        void shouldReturnEmptyPageWhenNoMoviesExist() {
            // Arrange
            Pageable pageable = Pageable.ofSize(10);
            Page<MovieEntity> emptyPage = new PageImpl<>(List.of());
            
            when(movieRepository.findAll(pageable))
                    .thenReturn(emptyPage);

            // Act
            Page<MovieResDTO> result = movieService.getAllMovies(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
            assertTrue(result.isEmpty());
            verify(movieRepository, times(1)).findAll(pageable);
        }

        @Test
        void shouldReturnMultipleMoviesWhenPaginationIsUsed() {
            // Arrange
            Pageable pageable = Pageable.ofSize(2);
            MovieEntity entity1 = MovieFactory.createMovieEntity(UUID.randomUUID(), "Movie 1");
            MovieEntity entity2 = MovieFactory.createMovieEntity(UUID.randomUUID(), "Movie 2");
            MovieResDTO dto1 = MovieFactory.createMovieResponseDTO(entity1.getId(), "Movie 1");
            MovieResDTO dto2 = MovieFactory.createMovieResponseDTO(entity2.getId(), "Movie 2");
            Page<MovieEntity> entityPage = new PageImpl<>(List.of(entity1, entity2));
            
            when(movieRepository.findAll(pageable))
                    .thenReturn(entityPage);
            when(movieMapper.toResponse(entity1))
                    .thenReturn(dto1);
            when(movieMapper.toResponse(entity2))
                    .thenReturn(dto2);

            // Act
            Page<MovieResDTO> result = movieService.getAllMovies(pageable);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            verify(movieRepository, times(1)).findAll(pageable);
            verify(movieMapper, times(2)).toResponse(any());
        }
    }

    @Nested
    class GetMovieById {

        @Test
        void shouldReturnMovieDTOWhenMovieExists() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            MovieEntity entity = MovieFactory.createMovieEntity(movieId, "Test Movie");
            MovieResDTO dto = MovieFactory.createMovieResponseDTO(movieId, "Test Movie");
            
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.of(entity));
            when(movieMapper.toResponse(entity))
                    .thenReturn(dto);

            // Act
            MovieResDTO result = movieService.getMovieById(movieId);

            // Assert
            assertNotNull(result);
            assertEquals(movieId, result.id());
            assertEquals("Test Movie", result.title());
            verify(movieRepository, times(1)).findById(movieId);
            verify(movieMapper, times(1)).toResponse(entity);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenMovieDoesNotExist() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                movieService.getMovieById(movieId);
            });
            verify(movieRepository, times(1)).findById(movieId);
            verify(movieMapper, never()).toResponse(any());
        }

        @Test
        void shouldThrowNotFoundExceptionWithCorrectMessageFormat() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                movieService.getMovieById(movieId);
            });
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(movieId.toString()));
        }
    }

    @Nested
    class CreateMovie {

        @Test
        void shouldCreateMovieSuccessfully() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            CreateMovieReqDTO requestDTO = MovieFactory.createMovieRequestDTO();
            MovieEntity entity = MovieFactory.createMovieEntity();
            entity.setTitle(requestDTO.title());
            entity.setDescription(requestDTO.description());
            entity.setDurationMinutes(requestDTO.durationMinutes());
            entity.setRating(requestDTO.rating());
            
            when(movieMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(movieRepository.saveAndFlush(any(MovieEntity.class)))
                    .thenReturn(entity);

            // Act
            UUID result = movieService.createMovie(requestDTO, userId);

            // Assert
            assertNotNull(result);
            assertEquals(entity.getId(), result);
            verify(movieMapper, times(1)).toEntity(requestDTO);
            verify(movieRepository, times(1)).saveAndFlush(any(MovieEntity.class));
        }

        @Test
        void shouldSetCreatedByUserIdWhenCreatingMovie() {
            // Arrange
            String userId = "550e8400-e29b-41d4-a716-446655440000";
            CreateMovieReqDTO requestDTO = MovieFactory.createMovieRequestDTO();
            MovieEntity entity = MovieFactory.createMovieEntity();
            
            when(movieMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(movieRepository.saveAndFlush(any(MovieEntity.class)))
                    .thenReturn(entity);

            // Act
            movieService.createMovie(requestDTO, userId);

            // Assert
            verify(movieRepository, times(1)).saveAndFlush(argThat(movie -> 
                    movie.getCreatedByUserId().equals(UUID.fromString(userId))
            ));
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenTitleAlreadyExists() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            CreateMovieReqDTO requestDTO = MovieFactory.createMovieRequestDTO();
            MovieEntity entity = MovieFactory.createMovieEntity();
            
            when(movieMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(movieRepository.saveAndFlush(any(MovieEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate"));

            // Act & Assert
            assertThrows(DuplicateResourceException.class, () -> {
                movieService.createMovie(requestDTO, userId);
            });
            verify(movieRepository, times(1)).saveAndFlush(any(MovieEntity.class));
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWithCorrectFieldName() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            CreateMovieReqDTO requestDTO = MovieFactory.createMovieRequestDTO();
            MovieEntity entity = MovieFactory.createMovieEntity();
            
            when(movieMapper.toEntity(requestDTO))
                    .thenReturn(entity);
            when(movieRepository.saveAndFlush(any(MovieEntity.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate"));

            // Act & Assert
            DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
                movieService.createMovie(requestDTO, userId);
            });
            assertTrue(exception.getMessage().contains("Titulo"));
        }
    }

    @Nested
    class DeleteMovieById {

        @Test
        void shouldDeleteMovieSuccessfully() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.softDeleteById(movieId))
                    .thenReturn(1);

            // Act & Assert
            assertDoesNotThrow(() -> {
                movieService.deleteMovieById(movieId);
            });
            verify(movieRepository, times(1)).softDeleteById(movieId);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenMovieDoesNotExistForDeletion() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.softDeleteById(movieId))
                    .thenReturn(0);

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                movieService.deleteMovieById(movieId);
            });
            verify(movieRepository, times(1)).softDeleteById(movieId);
        }

        @Test
        void shouldThrowNotFoundExceptionWithCorrectMessageForDelete() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.softDeleteById(movieId))
                    .thenReturn(0);

            // Act & Assert
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                movieService.deleteMovieById(movieId);
            });
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains(movieId.toString()));
        }

        @Test
        void shouldThrowNotFoundExceptionWhenAffectedRowsIsNegative() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            
            when(movieRepository.softDeleteById(movieId))
                    .thenReturn(-1);

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                movieService.deleteMovieById(movieId);
            });
        }
    }

    @Nested
    class UpdatePartialMovie {

        @Test
        void shouldUpdateMovieSuccessfully() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UpdateMovieReqDTO requestDTO = MovieFactory.createUpdateMovieRequestDTO();
            MovieEntity entity = MovieFactory.createMovieEntity(movieId, requestDTO.title());
            MovieResDTO responseDTO = MovieFactory.createMovieResponseDTO(movieId, requestDTO.title());
            
            when(movieRepository.updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating()))
                    .thenReturn(1);
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.of(entity));
            when(movieMapper.toResponse(entity))
                    .thenReturn(responseDTO);

            // Act
            MovieResDTO result = movieService.updatePartialMovie(movieId, requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(movieId, result.id());
            verify(movieRepository, times(1)).updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating());
            verify(movieRepository, times(1)).findById(movieId);
            verify(movieMapper, times(1)).toResponse(entity);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenMovieDoesNotExistForUpdate() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UpdateMovieReqDTO requestDTO = MovieFactory.createUpdateMovieRequestDTO();
            
            when(movieRepository.updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating()))
                    .thenReturn(0);

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                movieService.updatePartialMovie(movieId, requestDTO);
            });
            verify(movieRepository, times(1)).updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating());
            verify(movieRepository, never()).findById(any());
        }

        @Test
        void shouldThrowDuplicateResourceExceptionOnUpdateWhenTitleAlreadyExists() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UpdateMovieReqDTO requestDTO = MovieFactory.createUpdateMovieRequestDTO();
            
            when(movieRepository.updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating()))
                    .thenThrow(new DataIntegrityViolationException("Duplicate"));

            // Act & Assert
            assertThrows(DuplicateResourceException.class, () -> {
                movieService.updatePartialMovie(movieId, requestDTO);
            });
            verify(movieRepository, never()).findById(any());
        }

        @Test
        void shouldUpdateMovieWithPartialData() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UpdateMovieReqDTO partialDTO = MovieFactory.createPartialUpdateMovieRequestDTO("New Title");
            MovieEntity entity = MovieFactory.createMovieEntity(movieId, "New Title");
            MovieResDTO responseDTO = MovieFactory.createMovieResponseDTO(movieId, "New Title");
            
            when(movieRepository.updatePartial(
                    movieId,
                    partialDTO.title(),
                    partialDTO.description(),
                    partialDTO.durationMinutes(),
                    partialDTO.rating()))
                    .thenReturn(1);
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.of(entity));
            when(movieMapper.toResponse(entity))
                    .thenReturn(responseDTO);

            // Act
            MovieResDTO result = movieService.updatePartialMovie(movieId, partialDTO);

            // Assert
            assertNotNull(result);
            assertEquals("New Title", result.title());
            verify(movieRepository, times(1)).updatePartial(
                    movieId,
                    partialDTO.title(),
                    partialDTO.description(),
                    partialDTO.durationMinutes(),
                    partialDTO.rating());
        }

        @Test
        void shouldThrowNotFoundExceptionIfMovieIsNotFoundAfterUpdate() {
            // Arrange
            UUID movieId = UUID.randomUUID();
            UpdateMovieReqDTO requestDTO = MovieFactory.createUpdateMovieRequestDTO();
            
            when(movieRepository.updatePartial(
                    movieId,
                    requestDTO.title(),
                    requestDTO.description(),
                    requestDTO.durationMinutes(),
                    requestDTO.rating()))
                    .thenReturn(1);
            when(movieRepository.findById(movieId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> {
                movieService.updatePartialMovie(movieId, requestDTO);
            });
            verify(movieRepository, times(1)).findById(movieId);
            verify(movieMapper, never()).toResponse(any());
        }
    }
}