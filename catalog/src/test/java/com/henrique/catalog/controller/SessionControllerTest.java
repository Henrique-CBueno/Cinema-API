package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.sessions.CreateSessionReqDTO;
import com.henrique.catalog.domain.dto.req.sessions.GetAllSessionParamsDTO;
import com.henrique.catalog.domain.dto.res.movie.MovieResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.enums.SessionStatus;
import com.henrique.catalog.factory.MovieFactory;
import com.henrique.catalog.factory.RoomFactory;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.SessionService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    @Nested
    class GetAllSession {

        @Test
        void shouldGetAllSessionsWithHttpOK() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            GetAllSessionParamsDTO filterParams = new GetAllSessionParamsDTO(null, null, null, null);

            doReturn(buildSessionPage())
                    .when(sessionService)
                    .getSessions(paginationParams.toPageable(), filterParams);

            // Act
            ResponseEntity<SuccessListDataResponse> response = sessionController.getAllSession(
                    paginationParams,
                    filterParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(sessionService, times(1)).getSessions(paginationParams.toPageable(), filterParams);
        }

        @Test
        void shouldReturnNoContentWhenSessionsListIsEmpty() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            GetAllSessionParamsDTO filterParams = new GetAllSessionParamsDTO(null, null, null, null);

            doReturn(new PageImpl<>(List.of()))
                    .when(sessionService)
                    .getSessions(paginationParams.toPageable(), filterParams);

            // Act
            ResponseEntity<SuccessListDataResponse> response = sessionController.getAllSession(
                    paginationParams,
                    filterParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(sessionService, times(1)).getSessions(paginationParams.toPageable(), filterParams);
        }

        @Test
        void shouldPassCorrectParametersToService() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(1, 10);
            GetAllSessionParamsDTO filterParams = new GetAllSessionParamsDTO(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    LocalDate.of(2026, 2, 4));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            ArgumentCaptor<GetAllSessionParamsDTO> paramsCaptor = ArgumentCaptor.forClass(GetAllSessionParamsDTO.class);

            doReturn(new PageImpl<>(List.of()))
                    .when(sessionService)
                    .getSessions(pageableCaptor.capture(), paramsCaptor.capture());

            // Act
            sessionController.getAllSession(paginationParams, filterParams);

            // Assert
            assertEquals(paginationParams.page(), pageableCaptor.getValue().getPageNumber());
            assertEquals(paginationParams.pageSize(), pageableCaptor.getValue().getPageSize());
            assertEquals(filterParams, paramsCaptor.getValue());
        }

        @Test
        void shouldReturnResponseBodyCorrect() {
            // Arrange
            PaginationParams paginationParams = new PaginationParams(0, 5);
            GetAllSessionParamsDTO filterParams = new GetAllSessionParamsDTO(null, null, null, null);

            Page<SessionResDTO> returnedSessions = buildSessionPage();

            doReturn(returnedSessions)
                    .when(sessionService)
                    .getSessions(paginationParams.toPageable(), filterParams);

            var expectedResponse = ResponseEntity.ok(new SuccessListDataResponse(
                    returnedSessions.getContent(),
                    returnedSessions.getNumber(),
                    returnedSessions.getSize(),
                    returnedSessions.getTotalElements()));

            // Act
            ResponseEntity<SuccessListDataResponse> response = sessionController.getAllSession(
                    paginationParams,
                    filterParams);

            // Assert
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }
    }

    @Nested
    class GetSessionById {

        @Test
        void shouldReturnHttpOK() {
            // Arrange
            SessionResDTO session = buildSingleSession();

            doReturn(session)
                    .when(sessionService)
                    .getSessionById(any(UUID.class));

            // Act
            ResponseEntity<SuccessResponse> response = sessionController.getSession(session.id().toString());

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        void shouldPassCorrectParametersToService() {
            // Arrange
            UUID sessionId = UUID.randomUUID();
            SessionResDTO session = buildSingleSession();

            doReturn(session)
                    .when(sessionService)
                    .getSessionById(any(UUID.class));

            // Act
            sessionController.getSession(sessionId.toString());

            // Assert
            verify(sessionService, times(1)).getSessionById(sessionId);
        }

        @Test
        void shouldReturnCorrectResponseBody() {
            // Arrange
            SessionResDTO session = buildSingleSession();
            var expectedResponse = ResponseEntity.ok(new SuccessResponse(session));

            doReturn(session)
                    .when(sessionService)
                    .getSessionById(any(UUID.class));

            // Act
            ResponseEntity<SuccessResponse> response = sessionController.getSession(session.id().toString());

            // Assert
            assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("body.timestamp")
                    .isEqualTo(expectedResponse);
        }
    }

        @Nested
        class CreateSession {

                @Test
                void shouldReturnHttpCreated() {
                        // Arrange
                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/sessions");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        UUID createdId = UUID.randomUUID();
                        CreateSessionReqDTO dto = new CreateSessionReqDTO(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        LocalDateTime.of(2026, 2, 4, 14, 0),
                                        new BigDecimal("30.00")
                        );
                        String userId = UUID.randomUUID().toString();

                        doReturn(createdId)
                                        .when(sessionService)
                                        .createNewSession(dto, UUID.fromString(userId));

                        // Act
                        ResponseEntity<Void> response = sessionController.createSession(userId, dto);

                        // Assert
                        assertEquals(HttpStatus.CREATED, response.getStatusCode());
                        assertThat(response.getHeaders().getLocation()).isNotNull();

                        // Cleanup
                        RequestContextHolder.resetRequestAttributes();
                }

                @Test
                void shouldPassCorrectParametersToService() {
                        // Arrange
                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/sessions");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        UUID createdId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();
                        CreateSessionReqDTO dto = new CreateSessionReqDTO(
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        UUID.randomUUID(),
                                        LocalDateTime.of(2026, 2, 4, 14, 0),
                                        new BigDecimal("30.00")
                        );

                        doReturn(createdId)
                                        .when(sessionService)
                                        .createNewSession(any(CreateSessionReqDTO.class), any(UUID.class));

                        // Act
                        sessionController.createSession(userId.toString(), dto);

                        // Assert
                        verify(sessionService, times(1)).createNewSession(dto, userId);

                        // Cleanup
                        RequestContextHolder.resetRequestAttributes();
                }
        }

        @Nested
        class DeleteSession {

                @Test
                void shouldDeleteSessionWithHttpNoContent() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID sessionId = UUID.randomUUID();

                            doNothing()
                                    .when(sessionService)
                                    .deleteSeatFromSession(cinemaId, roomId, sessionId);

                        // Act
                        ResponseEntity<Void> response = sessionController.deleteSeat(
                                        cinemaId.toString(),
                                        roomId.toString(),
                                        sessionId.toString());

                        // Assert
                        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                }

                @Test
                void shouldPassCorrectParametersToService() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID sessionId = UUID.randomUUID();

                            doNothing()
                                    .when(sessionService)
                                    .deleteSeatFromSession(any(UUID.class), any(UUID.class), any(UUID.class));

                        // Act
                        sessionController.deleteSeat(cinemaId.toString(), roomId.toString(), sessionId.toString());

                        // Assert
                        verify(sessionService, times(1)).deleteSeatFromSession(cinemaId, roomId, sessionId);
                }

                @Test
                void shouldCallServiceDeleteMethod() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID sessionId = UUID.randomUUID();

                            doNothing()
                                    .when(sessionService)
                                    .deleteSeatFromSession(any(UUID.class), any(UUID.class), any(UUID.class));

                        // Act
                        sessionController.deleteSeat(cinemaId.toString(), roomId.toString(), sessionId.toString());

                        // Assert
                        verify(sessionService, times(1)).deleteSeatFromSession(any(UUID.class), any(UUID.class), any(UUID.class));
                }

                @Test
                void shouldDeleteDifferentSessionIds() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        UUID sessionId1 = UUID.randomUUID();
                        UUID sessionId2 = UUID.randomUUID();

                            doNothing()
                                    .when(sessionService)
                                    .deleteSeatFromSession(any(UUID.class), any(UUID.class), any(UUID.class));

                        // Act
                        sessionController.deleteSeat(cinemaId.toString(), roomId.toString(), sessionId1.toString());
                        sessionController.deleteSeat(cinemaId.toString(), roomId.toString(), sessionId2.toString());

                        // Assert
                        verify(sessionService, times(2)).deleteSeatFromSession(any(UUID.class), any(UUID.class), any(UUID.class));
                }
        }

    private Page<SessionResDTO> buildSessionPage() {
        MovieResDTO movie = MovieFactory.createMovieResponseDTO(UUID.randomUUID(), "Matrix");
        RoomsResDTO room = RoomFactory.createRoomsResponseDTO(UUID.randomUUID(), "Sala 1");

        SessionResDTO session = new SessionResDTO(
                UUID.randomUUID(),
                movie,
                room,
                LocalDateTime.of(2026, 2, 4, 14, 0),
                LocalDateTime.of(2026, 2, 4, 16, 0),
                new BigDecimal("30.00"),
                SessionStatus.SCHEDULED);

        return new PageImpl<>(List.of(session));
    }

    private SessionResDTO buildSingleSession() {
        MovieResDTO movie = MovieFactory.createMovieResponseDTO(UUID.randomUUID(), "Matrix");
        RoomsResDTO room = RoomFactory.createRoomsResponseDTO(UUID.randomUUID(), "Sala 1");

        return new SessionResDTO(
                UUID.randomUUID(),
                movie,
                room,
                LocalDateTime.of(2026, 2, 4, 14, 0),
                LocalDateTime.of(2026, 2, 4, 16, 0),
                new BigDecimal("30.00"),
                SessionStatus.SCHEDULED);
    }
}
