package com.bsys.custumers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakTokenService {

    private final WebClient webClient;

    @Value("${feign.clients.keycloak.url}")
    private String url;

    @Value("${feign.clients.keycloak.client-id}")
    private String clientId;

    @Value("${feign.clients.keycloak.client-secret}")
    private String clientSecret;

    @Value("${feign.clients.keycloak.realm}")
    private String realm;

    private String token;
    private Instant expiresAt;

    public synchronized String getToken() {

        if (token != null && Instant.now().isBefore(expiresAt)) {
            return token;
        }

        Map<String, Object> response = webClient.post()
                .uri(url + "/realms/" + realm + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=client_credentials" +
                                "&client_id=" + clientId +
                                "&client_secret=" + clientSecret
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        token = (String) response.get("access_token");
        int expiresIn = (Integer) response.get("expires_in");
        expiresAt = Instant.now().plusSeconds(expiresIn - 30);

        return token;
    }
}