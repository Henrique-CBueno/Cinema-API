package com.bsys.reservation.clients.catalog;

import com.bsys.reservation.clients.catalog.DTO.SeatResDTO;
import com.bsys.reservation.clients.catalog.DTO.SeatsExistenceResDTO;
import com.bsys.reservation.infra.padronize.SuccessListDataResponse;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalog-seats-client", url = "${clients.catalog.url}")
public interface SeatsClient {

    @PostMapping("cinemas/{cinemaId}/rooms/{roomId}/seats/exists")
    ResponseEntity<SeatsExistenceResDTO> checkSeatsExistence(@PathVariable String cinemaId,
                                                             @PathVariable String roomId,
                                                             @RequestBody List<UUID> seatIds);

    @GetMapping("cinemas/{cinemaId}/rooms/{roomId}/seats/{seatId}")
     ResponseEntity<SuccessResponse<SeatResDTO>> getSeatById(@PathVariable String cinemaId,
                                                             @PathVariable String roomId,
                                                             @PathVariable String seatId);
}
