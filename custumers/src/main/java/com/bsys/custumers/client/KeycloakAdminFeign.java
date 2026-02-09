package com.bsys.custumers.client;

import com.bsys.custumers.config.KeycloakFeignConfig;
import com.bsys.custumers.domain.dto.req.CreateKeycloakUserRequest;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "keycloak-admin",
        url = "${feign.clients.keycloak.url}",
        configuration = KeycloakFeignConfig.class
)
public interface KeycloakAdminFeign {

    @PostMapping("/admin/realms/{realm}/users")
    Response createUser(
            @PathVariable String realm,
            @RequestBody CreateKeycloakUserRequest body
    );

    @PutMapping("/admin/realms/{realm}/users/{id}/send-verify-email")
    Response sendVerifyEmail(
            @PathVariable String realm,
            @PathVariable("id") String userId
    );
}
