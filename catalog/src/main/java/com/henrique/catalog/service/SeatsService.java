package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.mapper.SeatMapper;
import com.henrique.catalog.repository.SeatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatsService {

    private final SeatsRepository seatsRepository;
    private final SeatMapper seatMapper;

    public Page<SeatResDTO> getSeatsByCinemaRoom(UUID roomId,
                                                 Pageable pageable) {

        return seatsRepository.findAllByRoomId(roomId, pageable)
                .map(seatMapper::toDTO);
    }

}
