package com.bsys.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Envolve a requisição para permitir saneamento e sobrescrita controlada de headers
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);

        // Remove headers sensíveis enviados pelo cliente (evita spoofing)
        requestWrapper.removeHeader("X-User-Id");
        requestWrapper.removeHeader("X-User-Email");
        requestWrapper.removeHeader("X-User-Name");
        requestWrapper.removeHeader("X-User-Admin");

        // Obtém autenticação (se houver)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();

            // Injeta valores confiáveis vindos do JWT
            String userId = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            String username = jwt.getClaimAsString("preferred_username");
            boolean isAdmin = jwtToken.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_GATEWAY_ADMIN".equals(authority.getAuthority()));

            if (userId != null) requestWrapper.addHeader("X-User-Id", userId);
            if (email != null) requestWrapper.addHeader("X-User-Email", email);
            if (username != null) requestWrapper.addHeader("X-User-Name", username);
            requestWrapper.addHeader("X-User-Admin", Boolean.toString(isAdmin));
        }

        // Encaminha sempre o wrapper: sem autenticação, headers chegam nulos; com JWT, chegam preenchidos
        filterChain.doFilter(requestWrapper, response);
    }
}
