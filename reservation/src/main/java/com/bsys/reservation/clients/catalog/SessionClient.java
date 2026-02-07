package com.bsys.reservation.clients.catalog;

import com.bsys.reservation.clients.catalog.DTO.SessionResDTO;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-session-client", url = "${clients.catalog.url}")
public interface SessionClient {

    @GetMapping("/sessions/{sessionId}")
    ResponseEntity<SuccessResponse<SessionResDTO>> getSession(@PathVariable String sessionId);
}
