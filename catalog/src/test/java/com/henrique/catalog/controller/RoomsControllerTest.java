package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.rooms.CreateRoomReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.factory.RoomFactory;
import com.henrique.catalog.factory.RoomResponseFactory;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.service.RoomsService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
                                        paginationParams);

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
                                        paginationParams);

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
                                        paginationParams);

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
                                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
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
                                        paginationParams);

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
                                        paginationParams1);
                        ResponseEntity<SuccessListDataResponse> response2 = roomsController.getAllRoomsFromCinemaId(
                                        cinemaId,
                                        paginationParams2);

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
                                        paginationParams);

                        // ASSERT
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        SuccessListDataResponse body = response.getBody();
                        assertThat(body).isNotNull();
                        assert body != null;
                        assertFalse(body.data().isEmpty());
                        assertThat(body.data().getFirst()).isInstanceOf(RoomsResDTO.class);
                }
        }

        @Nested
        class GetRoomFromCinemaByRoomId {

                @Test
                void shouldReturnRoomWithHttpOK() {
                        // ARRANGE
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        RoomsResDTO roomDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala 1");

                        doReturn(roomDTO)
                                        .when(roomsService)
                                        .getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // ACT
                        ResponseEntity<com.henrique.catalog.infra.padronize.SuccessResponse> response = roomsController
                                        .getRoomFromCinemaByRoomId(cinemaId, roomId);

                        // ASSERT
                        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(response.getBody()).isNotNull();
                    assert response.getBody() != null;
                    assertThat(response.getBody().data()).isEqualTo(roomDTO);
                        verify(roomsService, times(1)).getRoomByCinemaIdAndRoomId(cinemaId, roomId);
                }

                @Test
                void shouldPassCorrectCinemaIdAndRoomIdToService() {
                        // ARRANGE
                        UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
                        UUID roomId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
                        RoomsResDTO roomDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala VIP");

                        doReturn(roomDTO)
                                        .when(roomsService)
                                        .getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // ACT
                        roomsController.getRoomFromCinemaByRoomId(cinemaId, roomId);

                        // ASSERT
                        verify(roomsService, times(1)).getRoomByCinemaIdAndRoomId(
                                        eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                                        eq(UUID.fromString("660e8400-e29b-41d4-a716-446655440000")));
                }

                @Test
                void shouldReturnCorrectRoomData() {
                        // ARRANGE
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        RoomsResDTO roomDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala IMAX");

                        doReturn(roomDTO)
                                        .when(roomsService)
                                        .getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // ACT
                        ResponseEntity<com.henrique.catalog.infra.padronize.SuccessResponse> response = roomsController
                                        .getRoomFromCinemaByRoomId(cinemaId, roomId);

                        // ASSERT
                        assertThat(response.getBody()).isNotNull();
                    assert response.getBody() != null;
                    RoomsResDTO returnedRoom = (RoomsResDTO) response.getBody().data();
                        assertThat(returnedRoom.id()).isEqualTo(roomId);
                        assertThat(returnedRoom.name()).isEqualTo("Sala IMAX");
                }

                @Test
                void shouldReturnSuccessResponseWithCorrectStructure() {
                        // ARRANGE
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        RoomsResDTO roomDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala Premium");

                        doReturn(roomDTO)
                                        .when(roomsService)
                                        .getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // ACT
                        ResponseEntity<com.henrique.catalog.infra.padronize.SuccessResponse> response = roomsController
                                        .getRoomFromCinemaByRoomId(cinemaId, roomId);

                        // ASSERT
                        assertThat(response.getBody()).isNotNull();
                    assert response.getBody() != null;
                    assertThat(response.getBody().data()).isNotNull();
                        assertThat(response.getBody().timestamp()).isNotNull();
                }

                @Test
                void shouldCallServiceOnlyOnce() {
                        // ARRANGE
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        RoomsResDTO roomDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala 3D");

                        doReturn(roomDTO)
                                        .when(roomsService)
                                        .getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // ACT
                        roomsController.getRoomFromCinemaByRoomId(cinemaId, roomId);

                        // ASSERT
                        verify(roomsService, times(1)).getRoomByCinemaIdAndRoomId(any(), any());
                }
        }

        @Nested
        class CreateRoomForCinemaId {

                @Test
                void shouldReturnHttpCREATED() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/cinemas/" + cinemaId + "/rooms");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala 1", 10, 15);
                        UUID createdId = UUID.randomUUID();

                        doReturn(createdId)
                                        .when(roomsService)
                                        .createRoomForCinemaId(any(), any(), any());

                        // Act
                        ResponseEntity<Void> response = roomsController.createRoomForCinemaId(
                                        cinemaId,
                                        dto,
                                        UUID.randomUUID().toString());

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
                        String userId = UUID.randomUUID().toString();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala VIP", 12, 18);
                        UUID createdId = UUID.randomUUID();

                        doReturn(createdId)
                                        .when(roomsService)
                                        .createRoomForCinemaId(any(), any(), any());

                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/cinemas/" + cinemaId + "/rooms");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        // Act
                        roomsController.createRoomForCinemaId(cinemaId, dto, userId);

                        // Assert
                        verify(roomsService, times(1)).createRoomForCinemaId(
                                        eq(cinemaId),
                                        eq(dto),
                                        eq(UUID.fromString(userId)));

                        // Cleanup
                        RequestContextHolder.resetRequestAttributes();
                }

                @Test
                void shouldReturnCorrectLocationHeader() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/cinemas/" + cinemaId + "/rooms");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala IMAX", 20, 30);
                        UUID createdId = UUID.randomUUID();

                        doReturn(createdId)
                                        .when(roomsService)
                                        .createRoomForCinemaId(any(), any(), any());

                        // Act
                        ResponseEntity<Void> response = roomsController.createRoomForCinemaId(
                                        cinemaId,
                                        dto,
                                        UUID.randomUUID().toString());

                        // Assert
                        assertNotNull(response.getHeaders().getLocation());
                        assertTrue(response.getHeaders().getLocation().toString().contains(createdId.toString()));

                        // Cleanup
                        RequestContextHolder.resetRequestAttributes();
                }

                @Test
                void shouldCreateRoomWithDifferentData() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        String userId = UUID.randomUUID().toString();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala Premium", 25, 35);
                        UUID createdId = UUID.randomUUID();
                        ArgumentCaptor<CreateRoomReqDTO> dtoCaptor = ArgumentCaptor.forClass(CreateRoomReqDTO.class);

                        doReturn(createdId)
                                        .when(roomsService)
                                        .createRoomForCinemaId(eq(cinemaId), dtoCaptor.capture(),
                                                        eq(UUID.fromString(userId)));

                        MockHttpServletRequest request = new MockHttpServletRequest();
                        request.setRequestURI("/cinemas/" + cinemaId + "/rooms");
                        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

                        // Act
                        roomsController.createRoomForCinemaId(cinemaId, dto, userId);

                        // Assert
                        assertEquals("Sala Premium", dtoCaptor.getValue().name());
                        assertEquals(25, dtoCaptor.getValue().totalRows());
                        assertEquals(35, dtoCaptor.getValue().totalColumns());

                        // Cleanup
                        RequestContextHolder.resetRequestAttributes();
                }
        }

        @Nested
        class DeleteRoomFromCinema {

                @Test
                void shouldDeleteRoomWithHttpNO_CONTENT() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        doNothing().when(roomsService).deleteRoomFromCinema(roomId, cinemaId);

                        // Act
                        ResponseEntity<Void> response = roomsController.deleteRoomFromCinema(cinemaId, roomId);

                        // Assert
                        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                }

                @Test
                void shouldPassCorrectRoomIdAndCinemaIdToService() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        ArgumentCaptor<UUID> roomIdCaptor = ArgumentCaptor.forClass(UUID.class);
                        ArgumentCaptor<UUID> cinemaIdCaptor = ArgumentCaptor.forClass(UUID.class);

                        doNothing().when(roomsService).deleteRoomFromCinema(roomIdCaptor.capture(),
                                        cinemaIdCaptor.capture());

                        // Act
                        roomsController.deleteRoomFromCinema(cinemaId, roomId);

                        // Assert
                        assertEquals(roomId, roomIdCaptor.getValue());
                        assertEquals(cinemaId, cinemaIdCaptor.getValue());
                }

                @Test
                void shouldCallServiceDeleteMethod() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        doNothing().when(roomsService).deleteRoomFromCinema(roomId, cinemaId);

                        // Act
                        roomsController.deleteRoomFromCinema(cinemaId, roomId);

                        // Assert
                        verify(roomsService, times(1)).deleteRoomFromCinema(roomId, cinemaId);
                }

                @Test
                void shouldDeleteDifferentRoomIds() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId1 = UUID.randomUUID();
                        UUID roomId2 = UUID.randomUUID();

                        doNothing().when(roomsService).deleteRoomFromCinema(any(UUID.class), any(UUID.class));

                        // Act
                        roomsController.deleteRoomFromCinema(cinemaId, roomId1);
                        roomsController.deleteRoomFromCinema(cinemaId, roomId2);

                        // Assert
                        verify(roomsService, times(2)).deleteRoomFromCinema(any(UUID.class), any(UUID.class));
                }
        }
}
