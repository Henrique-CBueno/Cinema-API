package com.bsys.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI - permitir acesso público
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        
                        // Documentação dos microsserviços - permitir acesso público
                        .requestMatchers("/catalog/v3/api-docs/**", "/reservation/v3/api-docs/**", "/hello/v3/api-docs/**").permitAll()

                        // Operações administrativas do catálogo - requerem GATEWAY_ADMIN
                        .requestMatchers(HttpMethod.POST, "/catalog/movies").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/catalog/movies/{id}").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/catalog/movies/{id}").hasRole("GATEWAY_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/catalog/cinemas").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/catalog/cinemas/{id}").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/catalog/cinemas/{id}").hasRole("GATEWAY_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/catalog/cinemas/{id}/rooms").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/catalog/cinemas/{id}/rooms/{roomId}").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/catalog/cinemas/{id}/rooms/{roomId}").hasRole("GATEWAY_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/catalog/cinemas/{id}/rooms/{roomId}/seats").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/catalog/cinemas/{id}/rooms/{roomId}/seats/{seatId}").hasRole("GATEWAY_ADMIN")

                        .requestMatchers(HttpMethod.POST, "/catalog/sessions").hasRole("GATEWAY_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/catalog/sessions/{cinemaId}/{roomId}/{sessionId}").hasRole("GATEWAY_ADMIN")

                        .requestMatchers("/hello/**").hasRole("GATEWAY_ADMIN")
                        .requestMatchers("/catalog/**").permitAll()
                        .requestMatchers("/reservation/**").permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
        return http.build();
    }

    private JwtAuthenticationConverter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }

}
