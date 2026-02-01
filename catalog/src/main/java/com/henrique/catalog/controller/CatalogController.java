package com.henrique.catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("catalog")
public class CatalogController {

    @GetMapping("health")
    public ResponseEntity<?> health(
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        Map<String, Object> response = new HashMap<>();

        if (email != null) {
            // --- CENÁRIO 1: USUÁRIO LOGADO ---
            response.put("status", "Authenticated");
            response.put("message", "Bem-vindo de volta, " + username + "!");
            response.put("user_data", Map.of(
                    "id", userId,
                    "email", email
            ));

            // Aqui você poderia buscar dados específicos do usuário no banco
            // ex: service.getRecommendations(userId);

            return ResponseEntity.ok(response);
        }

        // --- CENÁRIO 2: ACESSO PÚBLICO ---
        response.put("status", "Public");
        response.put("message", "Acesso anônimo. Mostrando catálogo genérico.");

        return ResponseEntity.ok(response);
    }

}
