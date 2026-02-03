package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.rooms.CreateRoomReqDTO;
import com.henrique.catalog.domain.dto.req.rooms.UpdateRoomReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.mapper.RoomsMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.RoomsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomsService {

    private final RoomsMapper roomsMapper;
    private final RoomsRepository roomsRepository;
    private final CinemaService cinemaService;

    public Page<RoomsResDTO> getAllRooms(Pageable pageable,
                                         UUID cinemaId) {

        Page<RoomEntity> allRooms = roomsRepository.findByCinemaId(cinemaId,
                                                                    pageable);


        return allRooms.map(
                roomsMapper::toDTO
        );
    }

    public RoomsResDTO getRoomByCinemaIdAndRoomId(UUID cinemaId,
                                                  UUID roomId) {

        return roomsRepository.findByCinemaIdAndId(cinemaId, roomId).map(roomsMapper::toDTO)
                .orElseThrow(
                        () -> new NotFoundException(String.format(
                            ExceptionsConstants.ROOM_IN_CINEMA_DONT_EXISTS,
                            roomId,
                            cinemaId
                        ))
                );
    }

    public UUID createRoomForCinemaId(UUID cinemaId,
                                      CreateRoomReqDTO dto,
                                      UUID userId) {

        try {
            RoomEntity newRoom = roomsMapper.toEntity(dto);
            newRoom.setCinema(cinemaService.getCinemaByIdReturningEntity(cinemaId));
            newRoom.setCreatedByUserId(userId);

            RoomEntity createdRoom = roomsRepository.saveAndFlush(newRoom);
            return createdRoom.getId();

        } catch (DataIntegrityViolationException e) {

            throw new DuplicateResourceException(ExceptionsConstants.DUPLICATE_RESOURCE_ROOM, dto.name());
        }
    }

    @Transactional
    public RoomsResDTO updateRoom(UUID cinemaId,
                                  UUID roomId,
                                  UpdateRoomReqDTO dto) {

        try {
            int affectedRows = roomsRepository.updatePartial(cinemaId,
                    roomId,
                    dto.name(),
                    dto.totalRows(),
                    dto.totalColumns());

            if (affectedRows < 1) throw new NotFoundException(String.format(
                    ExceptionsConstants.ROOM_IN_CINEMA_DONT_EXISTS,
                    roomId,
                    cinemaId
            ));

            return roomsRepository.findByCinemaIdAndId(cinemaId, roomId).map(roomsMapper::toDTO)
                    .orElseThrow(
                    () -> new NotFoundException(String.format(
                            ExceptionsConstants.ROOM_IN_CINEMA_DONT_EXISTS,
                            roomId,
                            cinemaId
                    )
            ));
        } catch (DataIntegrityViolationException e) {

            throw new DuplicateResourceException(ExceptionsConstants.DUPLICATE_RESOURCE_ROOM, dto.name());
        }
    }
}
