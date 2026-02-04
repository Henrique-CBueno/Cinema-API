package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.rooms.CreateRoomReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.CinemaEntity;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.domain.mapper.RoomsMapper;
import com.henrique.catalog.factory.RoomFactory;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.RoomsRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomsServiceTest {

        @Mock
        private RoomsRepository roomsRepository;

        @Mock
        private CinemaMapper cinemaMapper;

        @Mock
        private RoomsMapper roomsMapper;

        @Mock
        private CinemaService cinemaService;

        @InjectMocks
        private RoomsService roomsService;

        @Nested
        class GetAllRooms {

                @Test
                void shouldReturnPageOfRoomsWhenRoomsExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity room1 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 1", cinemaEntity);
                        RoomEntity room2 = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala 2", cinemaEntity);

                        Page<RoomEntity> entityPage = new PageImpl<>(List.of(room1, room2));

                        RoomsResDTO dto1 = RoomFactory.createRoomsResponseDTO(room1.getId(), "Sala 1");
                        RoomsResDTO dto2 = RoomFactory.createRoomsResponseDTO(room2.getId(), "Sala 2");

                        when(roomsRepository.findByCinemaId(cinemaId, pageable))
                                        .thenReturn(entityPage);
                        when(roomsMapper.toDTO(room1))
                                        .thenReturn(dto1);
                        when(roomsMapper.toDTO(room2))
                                        .thenReturn(dto2);

                        // Act
                        Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(2, result.getTotalElements());
                        assertEquals("Sala 1", result.getContent().get(0).name());
                        assertEquals("Sala 2", result.getContent().get(1).name());
                        verify(roomsRepository, times(1)).findByCinemaId(cinemaId, pageable);
                        verify(roomsMapper, times(2)).toDTO(any(RoomEntity.class));
                }

                @Test
                void shouldReturnEmptyPageWhenNoRoomsExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);
                        Page<RoomEntity> emptyPage = new PageImpl<>(List.of());

                        when(roomsRepository.findByCinemaId(cinemaId, pageable))
                                        .thenReturn(emptyPage);

                        // Act
                        Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(0, result.getTotalElements());
                        assertTrue(result.getContent().isEmpty());
                        verify(roomsRepository, times(1)).findByCinemaId(cinemaId, pageable);
                        verify(roomsMapper, never()).toDTO(any());
                }

                @Test
                void shouldUseCorrectPaginationParameters() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(20).withPage(1);
                        Page<RoomEntity> emptyPage = new PageImpl<>(List.of());

                        when(roomsRepository.findByCinemaId(cinemaId, pageable))
                                        .thenReturn(emptyPage);

                        // Act
                        roomsService.getAllRooms(pageable, cinemaId);

                        // Assert
                        verify(roomsRepository, times(1)).findByCinemaId(eq(cinemaId), eq(pageable));
                }

                @Test
                void shouldReturnSingleRoomCorrectly() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        Pageable pageable = Pageable.ofSize(10);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity room = RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala Premium", cinemaEntity);

                        Page<RoomEntity> entityPage = new PageImpl<>(List.of(room));

                        RoomsResDTO dto = RoomFactory.createRoomsResponseDTO(room.getId(), "Sala Premium");

                        when(roomsRepository.findByCinemaId(cinemaId, pageable))
                                        .thenReturn(entityPage);
                        when(roomsMapper.toDTO(room))
                                        .thenReturn(dto);

                        // Act
                        Page<RoomsResDTO> result = roomsService.getAllRooms(pageable, cinemaId);

                        // Assert
                        assertEquals(1, result.getTotalElements());
                        assertEquals("Sala Premium", result.getContent().get(0).name());
                        assertEquals(10, result.getContent().get(0).totalRows());
                        assertEquals(15, result.getContent().get(0).totalColumns());
                }

                @Test
                void shouldCallRepositoryWithCorrectCinemaId() {
                        // Arrange
                        UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
                        Pageable pageable = Pageable.ofSize(10);
                        Page<RoomEntity> emptyPage = new PageImpl<>(List.of());

                        when(roomsRepository.findByCinemaId(cinemaId, pageable))
                                        .thenReturn(emptyPage);

                        // Act
                        roomsService.getAllRooms(pageable, cinemaId);

                        // Assert
                        verify(roomsRepository, times(1)).findByCinemaId(
                                        eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                                        any());
                }
        }

        @Nested
        class GetRoomByCinemaIdAndRoomId {

                @Test
                void shouldReturnRoomWhenExists() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala VIP", cinemaEntity);

                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(roomEntity));
                        when(roomsMapper.toDTO(roomEntity))
                                        .thenReturn(RoomFactory.createRoomsResponseDTO(roomId, "Sala VIP"));

                        // Act
                        RoomsResDTO result = roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(roomId, result.id());
                        assertEquals("Sala VIP", result.name());
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
                        verify(roomsMapper, times(1)).toDTO(roomEntity);
                }

                @Test
                void shouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.empty());

                        // Act & Assert
                        com.henrique.catalog.infra.exceptions.NotFoundException exception = assertThrows(
                                        com.henrique.catalog.infra.exceptions.NotFoundException.class,
                                        () -> roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId));

                        assertTrue(exception.getMessage().contains(roomId.toString()));
                        assertTrue(exception.getMessage().contains(cinemaId.toString()));
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
                        verify(roomsMapper, never()).toDTO(any());
                }

                @Test
                void shouldCallRepositoryWithCorrectParameters() {
                        // Arrange
                        UUID cinemaId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
                        UUID roomId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala 1", cinemaEntity);

                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(roomEntity));
                        when(roomsMapper.toDTO(roomEntity))
                                        .thenReturn(RoomFactory.createRoomsResponseDTO(roomId, "Sala 1"));

                        // Act
                        roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // Assert
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(
                                        eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                                        eq(UUID.fromString("660e8400-e29b-41d4-a716-446655440000")));
                }

                @Test
                void shouldMapRoomEntityToDTOCorrectly() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity roomEntity = RoomFactory.createRoomEntity(roomId, "Sala IMAX", cinemaEntity);
                        RoomsResDTO expectedDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala IMAX");

                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(roomEntity));
                        when(roomsMapper.toDTO(roomEntity))
                                        .thenReturn(expectedDTO);

                        // Act
                        RoomsResDTO result = roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId);

                        // Assert
                        assertEquals(expectedDTO, result);
                        assertEquals("Sala IMAX", result.name());
                }

                @Test
                void shouldThrowExceptionWithCorrectMessageFormat() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();

                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.empty());

                        // Act & Assert
                        com.henrique.catalog.infra.exceptions.NotFoundException exception = assertThrows(
                                        com.henrique.catalog.infra.exceptions.NotFoundException.class,
                                        () -> roomsService.getRoomByCinemaIdAndRoomId(cinemaId, roomId));

                        String expectedMessage = String.format("Não existe uma sala com id %s no cinema com id %s",
                                        roomId, cinemaId);
                        assertEquals(expectedMessage, exception.getMessage());
                }
        }

        @Nested
        class CreateRoomForCinemaId {

                @Test
                void shouldCreateRoomSuccessfully() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala 1", 10, 15);
                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity mappedEntity = new RoomEntity();
                        UUID createdId = UUID.randomUUID();
                        RoomEntity savedEntity = new RoomEntity();
                        savedEntity.setId(createdId);

                        when(roomsMapper.toEntity(dto))
                                        .thenReturn(mappedEntity);
                        when(cinemaService.getCinemaByIdReturningEntity(cinemaId))
                                        .thenReturn(cinemaEntity);
                        when(roomsRepository.saveAndFlush(any(RoomEntity.class)))
                                        .thenReturn(savedEntity);

                        // Act
                        UUID result = roomsService.createRoomForCinemaId(cinemaId, dto, userId);

                        // Assert
                        assertNotNull(result);
                        assertEquals(createdId, result);
                        verify(roomsMapper, times(1)).toEntity(dto);
                        verify(cinemaService, times(1)).getCinemaByIdReturningEntity(cinemaId);
                        verify(roomsRepository, times(1)).saveAndFlush(any(RoomEntity.class));
                }

                @Test
                void shouldSetCinemaAndCreatedByUserIdWhenCreatingRoom() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala Premium", 20, 25);
                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity mappedEntity = new RoomEntity();

                        when(roomsMapper.toEntity(dto))
                                        .thenReturn(mappedEntity);
                        when(cinemaService.getCinemaByIdReturningEntity(cinemaId))
                                        .thenReturn(cinemaEntity);
                        when(roomsRepository.saveAndFlush(any(RoomEntity.class)))
                                        .thenReturn(RoomFactory.createRoomEntity(UUID.randomUUID(), "Sala Premium"));

                        // Act
                        roomsService.createRoomForCinemaId(cinemaId, dto, userId);

                        // Assert
                        verify(roomsRepository, times(1))
                                        .saveAndFlush(argThat(room -> room.getCinema().equals(cinemaEntity)
                                                        && room.getCreatedByUserId().equals(userId)));
                }

                @Test
                void shouldThrowDuplicateResourceExceptionWhenRoomNameAlreadyExists() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala VIP", 12, 18);
                        RoomEntity mappedEntity = new RoomEntity();

                        when(roomsMapper.toEntity(dto))
                                        .thenReturn(mappedEntity);
                        when(cinemaService.getCinemaByIdReturningEntity(cinemaId))
                                        .thenReturn(RoomFactory.createCinemaEntity());
                        when(roomsRepository.saveAndFlush(any(RoomEntity.class)))
                                        .thenThrow(new DataIntegrityViolationException("Duplicate"));

                        // Act & Assert
                        assertThrows(DuplicateResourceException.class,
                                        () -> roomsService.createRoomForCinemaId(cinemaId, dto, userId));
                        verify(roomsRepository, times(1)).saveAndFlush(any(RoomEntity.class));
                }

                @Test
                void shouldThrowDuplicateResourceExceptionWithCorrectFieldName() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID userId = UUID.randomUUID();
                        CreateRoomReqDTO dto = new CreateRoomReqDTO("Sala IMAX", 18, 22);
                        RoomEntity mappedEntity = new RoomEntity();

                        when(roomsMapper.toEntity(dto))
                                        .thenReturn(mappedEntity);
                        when(cinemaService.getCinemaByIdReturningEntity(cinemaId))
                                        .thenReturn(RoomFactory.createCinemaEntity());
                        when(roomsRepository.saveAndFlush(any(RoomEntity.class)))
                                        .thenThrow(new DataIntegrityViolationException("Duplicate"));

                        // Act & Assert
                        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                                        () -> roomsService.createRoomForCinemaId(cinemaId, dto, userId));
                        assertTrue(exception.getMessage().contains("Ja existe uma sala com o nome"));
                }
        }

        @Nested
        class UpdateRoom {

                @Test
                void shouldUpdateRoomSuccessfully() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Atualizada", 12, 16);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity updatedRoom = RoomFactory.createRoomEntity(roomId, "Sala Atualizada", cinemaEntity);
                        updatedRoom.setTotalRows(12);
                        updatedRoom.setTotalColumns(16);

                        RoomsResDTO expectedDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala Atualizada");

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Atualizada", 12, 16))
                                        .thenReturn(1);
                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(updatedRoom));
                        when(roomsMapper.toDTO(updatedRoom))
                                        .thenReturn(expectedDTO);

                        // Act
                        RoomsResDTO result = roomsService.updateRoom(cinemaId, roomId, dto);

                        // Assert
                        assertNotNull(result);
                        assertEquals(expectedDTO, result);
                        assertEquals("Sala Atualizada", result.name());
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Sala Atualizada", 12, 16);
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
                        verify(roomsMapper, times(1)).toDTO(updatedRoom);
                }

                @Test
                void shouldThrowNotFoundExceptionWhenRoomDoesNotExist() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Nova", 10, 15);

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Nova", 10, 15))
                                        .thenReturn(0);

                        // Act & Assert
                        com.henrique.catalog.infra.exceptions.NotFoundException exception = assertThrows(
                                        com.henrique.catalog.infra.exceptions.NotFoundException.class,
                                        () -> roomsService.updateRoom(cinemaId, roomId, dto));

                        String expectedMessage = String.format("Não existe uma sala com id %s no cinema com id %s",
                                        roomId, cinemaId);
                        assertEquals(expectedMessage, exception.getMessage());
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Sala Nova", 10, 15);
                        verify(roomsRepository, never()).findByCinemaIdAndId(cinemaId, roomId);
                }

                @Test
                void shouldThrowNotFoundExceptionWhenRoomDoesNotExistAfterUpdate() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Modificada", 14, 18);

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Modificada", 14, 18))
                                        .thenReturn(1);
                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.empty());

                        // Act & Assert
                        com.henrique.catalog.infra.exceptions.NotFoundException exception = assertThrows(
                                        com.henrique.catalog.infra.exceptions.NotFoundException.class,
                                        () -> roomsService.updateRoom(cinemaId, roomId, dto));

                        assertEquals(String.format("Não existe uma sala com id %s no cinema com id %s",
                                        roomId, cinemaId), exception.getMessage());
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Sala Modificada", 14, 18);
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
                }

                @Test
                void shouldThrowDuplicateResourceExceptionWhenNameAlreadyExists() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Duplicada", 10, 15);

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Duplicada", 10, 15))
                                        .thenThrow(new DataIntegrityViolationException("Duplicate"));

                        // Act & Assert
                        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                                        () -> roomsService.updateRoom(cinemaId, roomId, dto));

                        assertTrue(exception.getMessage().contains("Ja existe uma sala com o nome"));
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Sala Duplicada", 10, 15);
                        verify(roomsRepository, never()).findByCinemaIdAndId(cinemaId, roomId);
                }

                @Test
                void shouldUpdateOnlyNameField() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Novo Nome", 15, 20);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity updatedRoom = RoomFactory.createRoomEntity(roomId, "Novo Nome", cinemaEntity);
                        updatedRoom.setTotalRows(15);
                        updatedRoom.setTotalColumns(20);

                        RoomsResDTO expectedDTO = RoomFactory.createRoomsResponseDTO(roomId, "Novo Nome");

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Novo Nome", 15, 20))
                                        .thenReturn(1);
                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(updatedRoom));
                        when(roomsMapper.toDTO(updatedRoom))
                                        .thenReturn(expectedDTO);

                        // Act
                        RoomsResDTO result = roomsService.updateRoom(cinemaId, roomId, dto);

                        // Assert
                        assertEquals("Novo Nome", result.name());
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Novo Nome", 15, 20);
                }

                @Test
                void shouldUpdateMultipleFields() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Premium Plus", 20, 25);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity updatedRoom = RoomFactory.createRoomEntity(roomId, "Sala Premium Plus",
                                        cinemaEntity);
                        updatedRoom.setTotalRows(20);
                        updatedRoom.setTotalColumns(25);

                        RoomsResDTO expectedDTO = RoomFactory.createRoomsResponseDTO(roomId, "Sala Premium Plus");

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Premium Plus", 20, 25))
                                        .thenReturn(1);
                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(updatedRoom));
                        when(roomsMapper.toDTO(updatedRoom))
                                        .thenReturn(expectedDTO);

                        // Act
                        RoomsResDTO result = roomsService.updateRoom(cinemaId, roomId, dto);

                        // Assert
                        assertNotNull(result);
                        assertEquals("Sala Premium Plus", result.name());
                        verify(roomsRepository, times(1)).updatePartial(cinemaId, roomId, "Sala Premium Plus", 20, 25);
                        verify(roomsRepository, times(1)).findByCinemaIdAndId(cinemaId, roomId);
                }

                @Test
                void shouldPassCorrectParametersToRepository() {
                        // Arrange
                        UUID cinemaId = UUID.randomUUID();
                        UUID roomId = UUID.randomUUID();
                        com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO dto = new com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO(
                                        "Sala Teste", 11, 17);

                        CinemaEntity cinemaEntity = RoomFactory.createCinemaEntity();
                        RoomEntity updatedRoom = RoomFactory.createRoomEntity(roomId, "Sala Teste", cinemaEntity);
                        updatedRoom.setTotalRows(11);
                        updatedRoom.setTotalColumns(17);

                        when(roomsRepository.updatePartial(cinemaId, roomId, "Sala Teste", 11, 17))
                                        .thenReturn(1);
                        when(roomsRepository.findByCinemaIdAndId(cinemaId, roomId))
                                        .thenReturn(java.util.Optional.of(updatedRoom));
                        when(roomsMapper.toDTO(updatedRoom))
                                        .thenReturn(RoomFactory.createRoomsResponseDTO(roomId, "Sala Teste"));

                        // Act
                        roomsService.updateRoom(cinemaId, roomId, dto);

                        // Assert
                        verify(roomsRepository, times(1)).updatePartial(
                                        eq(cinemaId),
                                        eq(roomId),
                                        eq("Sala Teste"),
                                        eq(11),
                                        eq(17));
                }
        }

        @Nested
        class SafeDeleteRoom {

                @Test
                void shouldDeleteRoomSuccessfullyWhenAffectedRowsIsGreaterThanZero() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        UUID cinemaId = UUID.randomUUID();

                        when(roomsRepository.softDeleteById(roomId, cinemaId))
                                        .thenReturn(1);

                        // Act
                        roomsService.deleteRoomFromCinema(roomId, cinemaId);

                        // Assert
                        verify(roomsRepository, times(1)).softDeleteById(roomId, cinemaId);
                }

                @Test
                void shouldThrowNotFoundExceptionWhenAffectedRowsIsZero() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        UUID cinemaId = UUID.randomUUID();

                        when(roomsRepository.softDeleteById(roomId, cinemaId))
                                        .thenReturn(0);

                        // Act & Assert
                        assertThrows(NotFoundException.class,
                                        () -> roomsService.deleteRoomFromCinema(roomId, cinemaId));
                        verify(roomsRepository, times(1)).softDeleteById(roomId, cinemaId);
                }

                @Test
                void shouldIncludeRoomIdAndCinemaIdInExceptionMessage() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        UUID cinemaId = UUID.randomUUID();

                        when(roomsRepository.softDeleteById(roomId, cinemaId))
                                        .thenReturn(0);

                        // Act & Assert
                        NotFoundException exception = assertThrows(NotFoundException.class,
                                        () -> roomsService.deleteRoomFromCinema(roomId, cinemaId));
                        assertTrue(exception.getMessage().contains(roomId.toString()));
                        assertTrue(exception.getMessage().contains(cinemaId.toString()));
                }

                @Test
                void shouldThrowNotFoundExceptionWhenAffectedRowsIsNegative() {
                        // Arrange
                        UUID roomId = UUID.randomUUID();
                        UUID cinemaId = UUID.randomUUID();

                        when(roomsRepository.softDeleteById(roomId, cinemaId))
                                        .thenReturn(-1);

                        // Act & Assert
                        assertThrows(NotFoundException.class,
                                        () -> roomsService.deleteRoomFromCinema(roomId, cinemaId));
                }
        }
}
