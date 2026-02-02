package com.henrique.catalog.controller;

import com.henrique.catalog.factory.MovieResponseFactory;
import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.movie.CreateMovieReqDTO;
import com.henrique.catalog.domain.dto.req.movie.UpdateMovieReqDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.MovieService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {
    
    @Mock
    private MovieService movieService;
    
    @InjectMocks
    private MovieController movieController;

    @Captor
    ArgumentCaptor<PageRequest> paginationCapture;

    @Captor
    ArgumentCaptor<UUID> uuidCapture;

    @Captor
    ArgumentCaptor<CreateMovieReqDTO> createMovieCaptor;

    @Captor
    ArgumentCaptor<String> userIdCaptor;

    @Captor
    ArgumentCaptor<UpdateMovieReqDTO> updateMovieCaptor;


    @Nested
    class getAllMovies {

        @Test
        void shouldGetAllMoviesWithHttpOK() {
            
            // ARRANGE
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(MovieResponseFactory.buildWithOneItem())
                    .when(movieService)
                    .getAllMovies(paginationParams.toPageable());
            
            // ACT
            ResponseEntity<SuccessListDataResponse> response = movieController.getAllMovies(paginationParams);

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void shouldGetAllMoviesWithHttpNO_CONTENT() {

            // ARRANGE
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(MovieResponseFactory.buildWithNoItem())
                    .when(movieService)
                    .getAllMovies(paginationParams.toPageable());

            // ACT
            ResponseEntity<SuccessListDataResponse> response = movieController.getAllMovies(paginationParams);

            // ASSERT
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {

            // ARRANGE
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(MovieResponseFactory.buildWithNoItem())
                    .when(movieService)
                    .getAllMovies(paginationCapture.capture());

            // ACT
            ResponseEntity<SuccessListDataResponse> response = movieController.getAllMovies(paginationParams);

            // ASSERT

            // Só vem um pois estou passando um objeto de parametro e nao dois parametros diferentes
            assertEquals(1, paginationCapture.getAllValues().size());
            assertEquals(paginationParams.page(), paginationCapture.getValue().getPageNumber());
            assertEquals(paginationParams.pageSize(), paginationCapture.getValue().getPageSize());
        }

        @Test
        void shouldReturnResponseBodyCorrect() {

            // ARRANGE
            PaginationParams paginationParams = new PaginationParams(0, 5);

            Page<MovieResDTO> returnedMovies = MovieResponseFactory.buildWithOneItem();

            doReturn(returnedMovies)
                    .when(movieService)
                    .getAllMovies(paginationParams.toPageable());

            var expectedResponse = ResponseEntity.ok(new SuccessListDataResponse(returnedMovies.getContent(),
                    returnedMovies.getNumber(),
                    returnedMovies.getSize(),
                    (long) returnedMovies.getNumberOfElements()));

            // ACT
            ResponseEntity<SuccessListDataResponse> response = movieController.getAllMovies(paginationParams);

            // ASSERT
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());

                // ver a resposta ignorando o timestamp
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }
    }

    @Nested
    class getMovieById {

        @Test
        void shouldReturnHttpOK() {

            // ARRANGE
            doReturn(MovieResponseFactory.buildFindMovieById())
                    .when(movieService)
                    .getMovieById(any());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.getMovieById(UUID.randomUUID());

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());

        }

        @Test
        void shouldPassCorrectParametersToService() {

            // ARRANGE
            UUID id = UUID.randomUUID();
            doReturn(MovieResponseFactory.buildFindMovieById())
                    .when(movieService)
                    .getMovieById(uuidCapture.capture());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.getMovieById(id);

            // ASSERT
            assertEquals(id, uuidCapture.getValue());
            assertEquals(1, uuidCapture.getAllValues().size());

        }

        @Test
        void shouldReturnCorrectResponseBody() {

            // ARRANGE
            MovieResDTO movie = MovieResponseFactory.buildFindMovieById();
            var expectedResponse = ResponseEntity.ok(
                new SuccessResponse(movie)
            );

            doReturn(movie)
                    .when(movieService)
                    .getMovieById(any());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.getMovieById(UUID.randomUUID());

            // ASSERT
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());

            // ver a resposta ignorando o timestamp
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);

        }

    }

    @Nested
    class createMovie {
        
        @Test
        void shouldReturnHttpCREATED() {

            // ARRANGE
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/movies");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();
            UUID createdId = UUID.randomUUID();

            doReturn(createdId)
                    .when(movieService)
                    .createMovie(any(), any());

            // ACT
            ResponseEntity<Void> response = movieController.createMovie(dto, "user-123");

            // ASSERT
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());

            // CLEANUP
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldPassCorrectParametersToService() {

            // ARRANGE
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/movies");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();
            String userId = "user-123";
            UUID createdId = UUID.randomUUID();

            doReturn(createdId)
                    .when(movieService)
                    .createMovie(createMovieCaptor.capture(), userIdCaptor.capture());

            // ACT
            ResponseEntity<Void> response = movieController.createMovie(dto, userId);

            // ASSERT
            assertEquals(dto, createMovieCaptor.getValue());
            assertEquals(userId, userIdCaptor.getValue());
            assertEquals(1, createMovieCaptor.getAllValues().size());
            assertEquals(1, userIdCaptor.getAllValues().size());

            // CLEANUP
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldReturnCorrectLocationHeader() {

            // ARRANGE
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/movies");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            CreateMovieReqDTO dto = MovieResponseFactory.buildCreateMovieRequest();
            UUID createdId = UUID.randomUUID();

            doReturn(createdId)
                    .when(movieService)
                    .createMovie(any(), any());

            // ACT
            ResponseEntity<Void> response = movieController.createMovie(dto, "user-123");

            // ASSERT
            assertNotNull(response.getHeaders().getLocation());
            assertTrue(response.getHeaders().getLocation().toString().contains(createdId.toString()));

            // CLEANUP
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Nested
    class deleteMovieById {
        
        @Test
        void shouldReturnHttpNO_CONTENT() {

            // ARRANGE
            UUID id = UUID.randomUUID();

            // ACT
            ResponseEntity<Void> response = movieController.deleteMovieById(id);

            // ASSERT
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {

            // ARRANGE
            UUID id = UUID.randomUUID();

            // ACT
            ResponseEntity<Void> response = movieController.deleteMovieById(id);

            // ASSERT
            verify(movieService, times(1)).deleteMovieById(uuidCapture.capture());
            assertEquals(id, uuidCapture.getValue());
            assertEquals(1, uuidCapture.getAllValues().size());
        }

        @Test
        void shouldCallServiceDeleteMethod() {

            // ARRANGE
            UUID id = UUID.randomUUID();

            // ACT
            ResponseEntity<Void> response = movieController.deleteMovieById(id);

            // ASSERT
            verify(movieService, times(1)).deleteMovieById(id);
        }
    }

    @Nested
    class partialUpdateMovie {
        
        @Test
        void shouldReturnHttpOK() {

            // ARRANGE
            UUID id = UUID.randomUUID();
            UpdateMovieReqDTO dto = MovieResponseFactory.buildPartialUpdateMovieRequest();

            doReturn(MovieResponseFactory.buildFindMovieById())
                    .when(movieService)
                    .updatePartialMovie(any(), any());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.partialUpdateMovie(id, dto);

            // ASSERT
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectParametersToService() {

            // ARRANGE
            UUID id = UUID.randomUUID();
            UpdateMovieReqDTO dto = MovieResponseFactory.buildUpdateMovieRequest();

            doReturn(MovieResponseFactory.buildFindMovieById())
                    .when(movieService)
                    .updatePartialMovie(uuidCapture.capture(), updateMovieCaptor.capture());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.partialUpdateMovie(id, dto);

            // ASSERT
            assertEquals(id, uuidCapture.getValue());
            assertEquals(dto, updateMovieCaptor.getValue());
            assertEquals(1, uuidCapture.getAllValues().size());
            assertEquals(1, updateMovieCaptor.getAllValues().size());
        }

        @Test
        void shouldReturnCorrectResponseBody() {

            // ARRANGE
            UUID id = UUID.randomUUID();
            UpdateMovieReqDTO dto = MovieResponseFactory.buildPartialUpdateMovieRequest();
            MovieResDTO updatedMovie = MovieResponseFactory.buildFindMovieById();
            var expectedResponse = ResponseEntity.ok(new SuccessResponse(updatedMovie));

            doReturn(updatedMovie)
                    .when(movieService)
                    .updatePartialMovie(any(), any());

            // ACT
            ResponseEntity<SuccessResponse> response = movieController.partialUpdateMovie(id, dto);

            // ASSERT
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());

            // ver a resposta ignorando o timestamp
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }
    }
}