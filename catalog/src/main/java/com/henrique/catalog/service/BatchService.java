package com.henrique.catalog.service;

import com.henrique.catalog.domain.dto.req.batch.BatchReserveReqDTO;
import com.henrique.catalog.domain.dto.res.batch.BatchResDTO;
import com.henrique.catalog.domain.dto.res.seat.SeatResDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.domain.entity.SeatEntity;
import com.henrique.catalog.domain.mapper.SeatMapper;
import com.henrique.catalog.domain.mapper.SessionMapper;
import com.henrique.catalog.infra.constants.ExceptionsConstants;
import com.henrique.catalog.infra.exceptions.NotFoundException;
import com.henrique.catalog.repository.SeatsRepository;
import com.henrique.catalog.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SeatsRepository seatsRepository;
    private final SeatMapper seatMapper;

    public List<BatchResDTO> getBatchReserves(List<BatchReserveReqDTO> dtos) {

        return dtos.stream()
                .map(
                        dto -> {
                            SeatResDTO seat = seatsRepository.findById(dto.seatId()).map(seatMapper::toDTO)
                                    .orElseThrow(
                                            () -> new NotFoundException(ExceptionsConstants.SEAT_IN_ROOM_DONT_EXISTS)
                                    );

                            SessionResDTO session = sessionRepository.findById(dto.sessionId()).map(sessionMapper::toDTO)
                                    .orElseThrow(
                                            () -> new NotFoundException(String.format(
                                                    ExceptionsConstants.SESSION_DONT_EXISTS,
                                                    dto.sessionId()
                                            ))
                                    );

                            return new BatchResDTO(dto.reserveId(),
                                    session,
                                    seat);
                        }
                ).toList();
    }
}
