package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.factory.SeatResponseFactory;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.service.SeatsService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatsControllerTest {

    @Mock
    private SeatsService seatsService;

    @InjectMocks
    private SeatsController seatsController;

    @Nested
    class GetAllSeatsByCinemaRoom {

        @Test
        void shouldGetAllSeatsWithHttpOK() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(SeatResponseFactory.buildWithOneItem())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assert response.getBody() != null;
            assertEquals(1, response.getBody().data().size());
            verify(seatsService, times(1)).getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());
        }

        @Test
        void shouldReturnNoContentWhenSeatsListIsEmpty() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(SeatResponseFactory.buildEmpty())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(seatsService, times(1)).getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());
        }

        @Test
        void shouldSendCorrectPaginationParametersToService() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(2, 20);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            doReturn(SeatResponseFactory.buildEmpty())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(eq(UUID.fromString(roomId)), pageableCaptor.capture());

            // Act
            seatsController.getAllSeatsByCinemaRoom(cinemaId, roomId, paginationParams);

            // Assert
            Pageable capturedPageable = pageableCaptor.getValue();
            assertEquals(2, capturedPageable.getPageNumber());
            assertEquals(20, capturedPageable.getPageSize());
        }

        @Test
        void shouldSendCorrectRoomIdToService() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            UUID roomId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(0, 10);
            ArgumentCaptor<UUID> roomIdCaptor = ArgumentCaptor.forClass(UUID.class);

            doReturn(SeatResponseFactory.buildEmpty())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(roomIdCaptor.capture(), any(Pageable.class));

            // Act
            seatsController.getAllSeatsByCinemaRoom(cinemaId, roomId.toString(), paginationParams);

            // Assert
            assertEquals(roomId, roomIdCaptor.getValue());
        }

        @Test
        void shouldReturnMultipleSeatsWithCorrectData() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(SeatResponseFactory.buildWithMultipleItems())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertNotNull(response.getBody());
            assertEquals(5, response.getBody().data().size());
        }

        @Test
        void shouldReturnSuccessListDataResponseWithCorrectStructure() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);
            Page<SeatResDTO> seatPage = SeatResponseFactory.buildWithMultipleItems();

            doReturn(seatPage)
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertNotNull(response.getBody());
            assertEquals(seatPage.getContent(), response.getBody().data());
            assertEquals(seatPage.getNumber(), response.getBody().page());
            assertEquals(seatPage.getSize(), response.getBody().pageSize());
            assertEquals(seatPage.getTotalElements(), response.getBody().totalElements());
        }

        @Test
        void shouldSupportDifferentPageSizes() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 50);

            doReturn(SeatResponseFactory.buildWithMultipleItems())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(seatsService, times(1)).getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());
        }

        @Test
        void shouldReturnSeatsWithDetailedInformation() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(SeatResponseFactory.buildWithMultipleItems())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            ResponseEntity<SuccessListDataResponse> response = seatsController.getAllSeatsByCinemaRoom(
                    cinemaId,
                    roomId,
                    paginationParams);

            // Assert
            assertNotNull(response.getBody());
            assertFalse(response.getBody().data().isEmpty());
            SeatResDTO firstSeat = (SeatResDTO) response.getBody().data().get(0);
            assertNotNull(firstSeat.id());
            assertNotNull(firstSeat.roomId());
            assertNotNull(firstSeat.rowLabel());
            assertNotNull(firstSeat.columnNumber());
        }

        @Test
        void shouldCallServiceOnlyOnce() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(SeatResponseFactory.buildWithOneItem())
                    .when(seatsService)
                    .getSeatsByCinemaRoom(UUID.fromString(roomId), paginationParams.toPageable());

            // Act
            seatsController.getAllSeatsByCinemaRoom(cinemaId, roomId, paginationParams);

            // Assert
            verify(seatsService, times(1)).getSeatsByCinemaRoom(any(UUID.class), any(Pageable.class));
        }
    }

    @Nested
    class CreateSeatsInCinemaRoom {

        @Test
        void shouldCreateSeatsWithHttpCREATED() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();

            List<CreateSeatReqDTO> seats = List.of(
                    new CreateSeatReqDTO("A", 1),
                    new CreateSeatReqDTO("A", 2));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/cinemas/" + cinemaId + "/rooms/" + roomId + "/seats");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            doNothing().when(seatsService).createSeatsInCinemaRoom(
                    UUID.fromString(cinemaId),
                    UUID.fromString(roomId),
                    seats,
                    UUID.fromString(userId));

            // Act
            ResponseEntity<Void> response = seatsController.createSeatsInCinemaRoom(
                    cinemaId,
                    roomId,
                    seats,
                    userId);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getHeaders().getLocation());

            // Cleanup
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldPassCorrectParametersToService() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            List<CreateSeatReqDTO> seats = List.of(
                    new CreateSeatReqDTO("A", 1));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/cinemas/" + cinemaId + "/rooms/" + roomId + "/seats");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            ArgumentCaptor<UUID> cinemaCaptor = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<UUID> roomCaptor = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<List> seatsCaptor = ArgumentCaptor.forClass(List.class);
            ArgumentCaptor<UUID> userCaptor = ArgumentCaptor.forClass(UUID.class);

            doNothing().when(seatsService).createSeatsInCinemaRoom(
                    cinemaCaptor.capture(),
                    roomCaptor.capture(),
                    seatsCaptor.capture(),
                    userCaptor.capture());

            // Act
            seatsController.createSeatsInCinemaRoom(
                    cinemaId.toString(),
                    roomId.toString(),
                    seats,
                    userId.toString());

            // Assert
            assertEquals(cinemaId, cinemaCaptor.getValue());
            assertEquals(roomId, roomCaptor.getValue());
            assertEquals(seats, seatsCaptor.getValue());
            assertEquals(userId, userCaptor.getValue());

            // Cleanup
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldCallServiceCreateMethod() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();

            List<CreateSeatReqDTO> seats = List.of(
                    new CreateSeatReqDTO("A", 1));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/cinemas/" + cinemaId + "/rooms/" + roomId + "/seats");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            doNothing().when(seatsService).createSeatsInCinemaRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(List.class),
                    any(UUID.class));

            // Act
            seatsController.createSeatsInCinemaRoom(cinemaId, roomId, seats, userId);

            // Assert
            verify(seatsService, times(1)).createSeatsInCinemaRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(List.class),
                    any(UUID.class));

            // Cleanup
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldReturnLocationHeader() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();

            List<CreateSeatReqDTO> seats = List.of(
                    new CreateSeatReqDTO("B", 5));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/cinemas/" + cinemaId + "/rooms/" + roomId + "/seats");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            doNothing().when(seatsService).createSeatsInCinemaRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(List.class),
                    any(UUID.class));

            // Act
            ResponseEntity<Void> response = seatsController.createSeatsInCinemaRoom(
                    cinemaId,
                    roomId,
                    seats,
                    userId);

            // Assert
            assertNotNull(response.getHeaders().getLocation());
            assertTrue(response.getHeaders().getLocation().toString().contains(roomId));

            // Cleanup
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void shouldCreateMultipleSeats() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String userId = UUID.randomUUID().toString();

            List<CreateSeatReqDTO> seats = List.of(
                    new CreateSeatReqDTO("A", 1),
                    new CreateSeatReqDTO("A", 2),
                    new CreateSeatReqDTO("B", 1),
                    new CreateSeatReqDTO("B", 2),
                    new CreateSeatReqDTO("C", 1));

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/cinemas/" + cinemaId + "/rooms/" + roomId + "/seats");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            doNothing().when(seatsService).createSeatsInCinemaRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(List.class),
                    any(UUID.class));

            // Act
            ResponseEntity<Void> response = seatsController.createSeatsInCinemaRoom(
                    cinemaId,
                    roomId,
                    seats,
                    userId);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(seatsService, times(1)).createSeatsInCinemaRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(List.class),
                    any(UUID.class));

            // Cleanup
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Nested
    class DeleteSeat {

        @Test
        void shouldDeleteSeatWithHttpNO_CONTENT() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String seatId = UUID.randomUUID().toString();

            doNothing().when(seatsService).deleteSeatFromRoom(
                    UUID.fromString(cinemaId),
                    UUID.fromString(roomId),
                    UUID.fromString(seatId));

            // Act
            ResponseEntity<Void> response = seatsController.deleteSeat(cinemaId, roomId, seatId);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        void shouldPassCorrectIdsToService() {
            // Arrange
            UUID cinemaId = UUID.randomUUID();
            UUID roomId = UUID.randomUUID();
            UUID seatId = UUID.randomUUID();

            ArgumentCaptor<UUID> cinemaCaptor = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<UUID> roomCaptor = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<UUID> seatCaptor = ArgumentCaptor.forClass(UUID.class);

            doNothing().when(seatsService).deleteSeatFromRoom(
                    cinemaCaptor.capture(),
                    roomCaptor.capture(),
                    seatCaptor.capture());

            // Act
            seatsController.deleteSeat(cinemaId.toString(), roomId.toString(), seatId.toString());

            // Assert
            assertEquals(cinemaId, cinemaCaptor.getValue());
            assertEquals(roomId, roomCaptor.getValue());
            assertEquals(seatId, seatCaptor.getValue());
        }

        @Test
        void shouldCallServiceDeleteMethod() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String seatId = UUID.randomUUID().toString();

            doNothing().when(seatsService).deleteSeatFromRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(UUID.class));

            // Act
            seatsController.deleteSeat(cinemaId, roomId, seatId);

            // Assert
            verify(seatsService, times(1)).deleteSeatFromRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(UUID.class));
        }

        @Test
        void shouldDeleteDifferentSeatIds() {
            // Arrange
            String cinemaId = UUID.randomUUID().toString();
            String roomId = UUID.randomUUID().toString();
            String seatId1 = UUID.randomUUID().toString();
            String seatId2 = UUID.randomUUID().toString();

            doNothing().when(seatsService).deleteSeatFromRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(UUID.class));

            // Act
            seatsController.deleteSeat(cinemaId, roomId, seatId1);
            seatsController.deleteSeat(cinemaId, roomId, seatId2);

            // Assert
            verify(seatsService, times(2)).deleteSeatFromRoom(
                    any(UUID.class),
                    any(UUID.class),
                    any(UUID.class));
        }
    }
}
