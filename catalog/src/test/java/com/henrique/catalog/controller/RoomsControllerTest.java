package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.factory.RoomResponseFactory;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.service.RoomsService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomsControllerTest {

    @Mock
    private RoomsService roomsService;

    @InjectMocks
    private RoomsController roomsController;

    @Nested
    class GetAllRoomsFromCinemaId {

        @Test
        void shouldGetAllRoomsWithHttpOK() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(RoomResponseFactory.buildWithOneItem())
                    .when(roomsService)
                    .getAllRooms(paginationParams.toPageable(), cinemaId);

            // ACT
            ResponseEntity<SuccessListDataResponse> response = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams
            );

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assert response.getBody() != null;
            assertEquals(1, response.getBody().data().size());
            verify(roomsService, times(1)).getAllRooms(paginationParams.toPageable(), cinemaId);
        }

        @Test
        void shouldReturnNoContentWhenRoomsListIsEmpty() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(RoomResponseFactory.buildEmpty())
                    .when(roomsService)
                    .getAllRooms(paginationParams.toPageable(), cinemaId);

            // ACT
            ResponseEntity<SuccessListDataResponse> response = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams
            );

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(roomsService, times(1)).getAllRooms(paginationParams.toPageable(), cinemaId);
        }

        @Test
        void shouldReturnMultipleRoomsWithCorrectData() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(0, 10);

            doReturn(RoomResponseFactory.buildWithMultipleItems())
                    .when(roomsService)
                    .getAllRooms(paginationParams.toPageable(), cinemaId);

            // ACT
            ResponseEntity<SuccessListDataResponse> response = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams
            );

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assert response.getBody() != null;
            assertEquals(3, response.getBody().data().size());
            verify(roomsService, times(1)).getAllRooms(paginationParams.toPageable(), cinemaId);
        }

        @Test
        void shouldPassCorrectCinemaIdToService() {
            // ARRANGE
            UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(RoomResponseFactory.buildWithOneItem())
                    .when(roomsService)
                    .getAllRooms(any(), any());

            // ACT
            roomsController.getAllRoomsFromCinemaId(cinemaId, paginationParams);

            // ASSERT
            verify(roomsService, times(1)).getAllRooms(
                    paginationParams.toPageable(),
                    UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
            );
        }

        @Test
        void shouldReturnSuccessListDataResponseWithCorrectStructure() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(1, 20);
            Page<RoomsResDTO> roomsPage = RoomResponseFactory.buildWithOneItem();

            doReturn(roomsPage)
                    .when(roomsService)
                    .getAllRooms(paginationParams.toPageable(), cinemaId);

            // ACT
            ResponseEntity<SuccessListDataResponse> response = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams
            );

            // ASSERT
            SuccessListDataResponse body = response.getBody();
            assertThat(body).isNotNull();
            assert body != null;
            assertThat(body.data()).isNotNull();
            assertThat(body.page()).isEqualTo(0);
            assertThat(body.pageSize()).isEqualTo(1);
            assertThat(body.totalElements()).isEqualTo(1);
        }

        @Test
        void shouldHandleDifferentPaginationSizes() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams1 = new PaginationParams(0, 5);
            PaginationParams paginationParams2 = new PaginationParams(0, 50);

            doReturn(RoomResponseFactory.buildWithOneItem())
                    .when(roomsService)
                    .getAllRooms(any(), eq(cinemaId));

            // ACT
            ResponseEntity<SuccessListDataResponse> response1 = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams1
            );
            ResponseEntity<SuccessListDataResponse> response2 = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams2
            );

            // ASSERT
            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(roomsService, times(2)).getAllRooms(any(), eq(cinemaId));
        }

        @Test
        void shouldReturnContentWithRoomDetailsWhenRoomsExist() {
            // ARRANGE
            UUID cinemaId = UUID.randomUUID();
            PaginationParams paginationParams = new PaginationParams(0, 5);

            doReturn(RoomResponseFactory.buildWithMultipleItems())
                    .when(roomsService)
                    .getAllRooms(paginationParams.toPageable(), cinemaId);

            // ACT
            ResponseEntity<SuccessListDataResponse> response = roomsController.getAllRoomsFromCinemaId(
                    cinemaId,
                    paginationParams
            );

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            SuccessListDataResponse body = response.getBody();
            assertThat(body).isNotNull();
            assert body != null;
            assertFalse(body.data().isEmpty());
            assertThat(body.data().getFirst()).isInstanceOf(RoomsResDTO.class);
        }
    }
}
