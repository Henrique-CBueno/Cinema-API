package com.bsys.custumers.service;

import com.bsys.custumers.client.KeycloakAdminFeign;
import com.bsys.custumers.domain.dto.req.CreateKeycloakUserRequest;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final KeycloakAdminFeign adminFeign;

    @Value("${feign.clients.keycloak.realm}")
    private String realm;

    public String register(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        var req = new CreateKeycloakUserRequest();
        req.setUsername(email);
        req.setEmail(email);
        req.setEnabled(true);
        req.setEmailVerified(false);
        req.setRequiredActions(List.of("VERIFY_EMAIL", "UPDATE_PROFILE", "UPDATE_PASSWORD"));

        Response response = adminFeign.createUser(realm, req);
        String userId = extractUserId(response);

        Response verifyEmailResponse = adminFeign.sendVerifyEmail(realm, userId);
        validateVerifyEmailResponse(verifyEmailResponse);

        return userId;
    }

    private String extractUserId(Response response) {
        if (response.status() != 201) {
            String body = tryReadBody(response);
            throw new IllegalStateException(
                    "Erro ao registrar usuario. Status: " + response.status() + (body.isBlank() ? "" : ". Body: " + body)
            );
        }

        String location = response.headers()
                .get("Location")
                .iterator()
                .next();

        return location.substring(location.lastIndexOf("/") + 1);
    }

    private String tryReadBody(Response response) {
        if (response.body() == null) {
            return "";
        }

        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch (IOException ignored) {
            return "";
        }
    }

    private void validateVerifyEmailResponse(Response response) {
        if (response.status() != 204) {
            String body = tryReadBody(response);
            throw new IllegalStateException(
                    "Erro ao enviar email de verificacao. Status: " + response.status() + (body.isBlank() ? "" : ". Body: " + body)
            );
        }
    }
}