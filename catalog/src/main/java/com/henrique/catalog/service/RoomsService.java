package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.mapper.RoomsMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.RoomsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomsService {

    private final RoomsMapper roomsMapper;
    private final RoomsRepository roomsRepository;

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
}
