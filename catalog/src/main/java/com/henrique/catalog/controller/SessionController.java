package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.global.PaginationParams;
import com.henrique.catalog.domain.dto.req.sessions.GetAllSessionParamsDTO;
import com.henrique.catalog.domain.dto.res.session.SessionResDTO;
import com.henrique.catalog.infra.padronize.SuccessListDataResponse;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<SuccessListDataResponse> getAllSession(PaginationParams paginationParams,
                                                                 GetAllSessionParamsDTO getAllSessionParamsDTO) {

        Page<SessionResDTO> sessions = sessionService.getSessions(paginationParams.toPageable(),
                getAllSessionParamsDTO);

        if (sessions.getContent().isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new SuccessListDataResponse(sessions.getContent(),
                sessions.getNumber(),
                sessions.getSize(),
                sessions.getTotalElements()));
    }

    @GetMapping("{sessionId}")
    public ResponseEntity<SuccessResponse> getSession(@PathVariable String sessionId) {

        SessionResDTO session = sessionService.getSessionById(UUID.fromString(sessionId));

        return ResponseEntity.ok(new SuccessResponse(session));
    }
}
