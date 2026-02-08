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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SeatsRepository seatsRepository;
    private final SeatMapper seatMapper;

    public List<BatchResDTO> getBatchReserves(List<BatchReserveReqDTO> dtos) {

        // Extract IDS to Set
        Set<UUID> seatIds = dtos.stream()
                .map(BatchReserveReqDTO::seatId)
                .collect(Collectors.toSet());

        Set<UUID> sessionIds = dtos.stream()
                .map(BatchReserveReqDTO::sessionId)
                .collect(Collectors.toSet());


        // Find All set in one query
        Map<UUID, SeatResDTO> seatsById =
                seatsRepository.findAllById(seatIds).stream()
                        .map(seatMapper::toDTO)
                        .collect(Collectors.toMap(SeatResDTO::id, Function.identity()));

        Map<UUID, SessionResDTO> sessionsById =
                sessionRepository.findAllById(sessionIds).stream()
                        .map(sessionMapper::toDTO)
                        .collect(Collectors.toMap(SessionResDTO::id, Function.identity()));

        return dtos.stream()
                .map(dto -> {

                    // Get seat By Map
                    SeatResDTO seat = seatsById.get(dto.seatId());
                    if (seat == null) {
                        throw new NotFoundException(
                                ExceptionsConstants.SEAT_IN_ROOM_DONT_EXISTS
                        );
                    }

                    // Get session by map
                    SessionResDTO session = sessionsById.get(dto.sessionId());
                    if (session == null) {
                        throw new NotFoundException(
                                String.format(
                                        ExceptionsConstants.SESSION_DONT_EXISTS,
                                        dto.sessionId()
                                )
                        );
                    }

                    return new BatchResDTO(
                            dto.reserveId(),
                            session,
                            seat
                    );
                })
                .toList();
    }
}
