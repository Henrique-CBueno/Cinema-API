package com.bsys.custumers.controller;

import com.bsys.custumers.domain.dto.req.RegisterRequest;
import com.bsys.custumers.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService service;

    @PostMapping
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {

        String keycloakId = service.register(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(keycloakId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }
}
