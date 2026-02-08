package com.henrique.catalog.controller;

import com.henrique.catalog.domain.dto.req.batch.BatchReserveReqDTO;
import com.henrique.catalog.domain.dto.res.batch.BatchResDTO;
import com.henrique.catalog.infra.padronize.SuccessResponse;
import com.henrique.catalog.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping("reserves")
    public ResponseEntity<SuccessResponse> getReservesBatch(@RequestBody List<BatchReserveReqDTO> dtos) {

        if (dtos.isEmpty()) return ResponseEntity.noContent().build();

        List<BatchResDTO> batchReserves = batchService.getBatchReserves(dtos);

        return ResponseEntity.ok(new SuccessResponse(batchReserves));
    }
}
