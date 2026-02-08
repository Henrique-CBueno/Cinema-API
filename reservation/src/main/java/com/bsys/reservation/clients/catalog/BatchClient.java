package com.bsys.reservation.clients.catalog;

import com.bsys.reservation.clients.catalog.DTO.BatchResDTO;
import com.bsys.reservation.clients.catalog.DTO.BatchReserveReqDTO;
import com.bsys.reservation.infra.padronize.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "catalog-batch-client", url = "${clients.batch.url}")
public interface BatchClient {

    @PostMapping("reserves")
    ResponseEntity<SuccessResponse<List<BatchResDTO>>> getReservesBatch(@RequestBody List<BatchReserveReqDTO> dtos);
}
