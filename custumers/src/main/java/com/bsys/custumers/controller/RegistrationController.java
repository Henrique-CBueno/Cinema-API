package com.bsys.custumers.controller;

import com.bsys.custumers.domain.dto.req.RegisterRequest;
import com.bsys.custumers.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService service;

    @PostMapping
    public ResponseEntity<Map<String, String>> register(
            @RequestBody RegisterRequest request
    ) {
        String id = service.register(request.getEmail());
        return ResponseEntity.ok(Map.of("keycloakId", id));
    }
}
