package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
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
}
