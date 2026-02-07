package com.bsys.reservation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "oauth2";
        String authUrl = "http://localhost:9090/realms/auth/protocol/openid-connect/auth";
        String tokenUrl = "http://localhost:9090/realms/auth/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("Reservation Service API")
                        .version("1.0")
                        .description("API de gerenciamento de reservas de cinemas e filmes"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl(authUrl)
                                                        .tokenUrl(tokenUrl)
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID Connect scope")
                                                                .addString("profile", "User profile")
                                                                .addString("email", "User email"))))))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/reservation")
                                .description("Gateway - Ambiente de Desenvolvimento")
                ));
    }
}
