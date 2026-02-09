package com.bsys.custumers.config;

import com.bsys.custumers.service.KeycloakTokenService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
public class KeycloakFeignConfig {

    private final KeycloakTokenService tokenService;

    @Bean
    public RequestInterceptor authInterceptor() {
        return request -> {
            String token = tokenService.getToken();
            request.header("Authorization", "Bearer " + token);
        };
    }
}
