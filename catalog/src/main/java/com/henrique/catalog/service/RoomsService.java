package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.cinema.CinemaResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsWithoutCinemaResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.mapper.CinemaMapper;
import com.henrique.catalog.domain.mapper.RoomsMapper;
import com.henrique.catalog.repository.RoomsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomsService {

    private final CinemaMapper cinemaMapper;
    private final RoomsMapper roomsMapper;
    private final RoomsRepository roomsRepository;

    public Page<RoomsResDTO> getAllRooms(Pageable pageable,
                                         UUID cinemaId) {

        Page<RoomEntity> allRooms = roomsRepository.findByCinemaId(cinemaId,
                                                                    pageable);


        return allRooms.map(
                room -> {
                    CinemaResDTO cinemaDto = cinemaMapper.toDTO(room.getCinema());

                    return new RoomsResDTO(room.getId(),
                            cinemaDto,
                            room.getName(),
                            room.getTotalRows(),
                            room.getTotalColumns());
                }
        );
    }
}
