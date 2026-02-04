package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.seat.CreateSeatReqDTO;
import com.henrique.catalog.domain.dto.res.rooms.RoomsResDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.entity.RoomEntity;
import com.henrique.catalog.domain.entity.SeatEntity;
import com.henrique.catalog.domain.mapper.SeatMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.DuplicateResourceException;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.infra.exceptions.UnprocessableEntityException;
import com.henrique.catalog.repository.SeatsRepository;
import io.micrometer.observation.Observation;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatsService {

    private final RoomsService roomsService;

    private final SeatsRepository seatsRepository;
    private final SeatMapper seatMapper;

    public Page<SeatResDTO> getSeatsByCinemaRoom(UUID roomId,
            Pageable pageable) {

        return seatsRepository.findAllByRoomId(roomId, pageable)
                .map(seatMapper::toDTO);
    }

    @Transactional
    public void createSeatsInCinemaRoom(UUID cinemaId,
            UUID roomId,
            List<CreateSeatReqDTO> seats,
            UUID userId) {

        try {
            RoomEntity room = roomsService.getRoomByCinemaIdAndRoomIdReturningEntity(cinemaId, roomId);
            Integer totalColumnsAvailableInRoom = room.getTotalColumns();
            Integer totalRowsAvailableInRoom = room.getTotalRows();

            List<SeatEntity> newSeats = seats.stream().map(
                    seat -> {

                        int rowNum = rowLabelToNumber(seat.rowLabel());

                        if (seat.columnNumber() > totalColumnsAvailableInRoom || rowNum > totalRowsAvailableInRoom) {
                            throw new UnprocessableEntityException(ExceptionsConstants.IMPOSSIBLE_SEAT_POSITION);
                        }

                        SeatEntity entity = seatMapper.toEntity(seat);
                        entity.setRoom(room);
                        entity.setCreatedByUserId(userId);

                        return entity;
                    }).toList();

            seatsRepository.saveAllAndFlush(newSeats);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(ExceptionsConstants.DUPLICATE_SEAT_POSITION);
        }

    }

    private int rowLabelToNumber(@NotBlank String label) {
        if (label == null || label.isEmpty())
            return 0;
        return label.toUpperCase().charAt(0) - 64;
    }

    public void deleteSeatFromRoom(UUID cinemaId,
            UUID roomId,
            UUID seatId) {

        int affectedRows = seatsRepository.softDeleteById(seatId, roomId, cinemaId);

        if (affectedRows < 1)
            throw new NotFoundException(String.format(
                    ExceptionsConstants.SEAT_IN_ROOM_DONT_EXISTS,
                    seatId,
                    roomId,
                    cinemaId));
    }

}
