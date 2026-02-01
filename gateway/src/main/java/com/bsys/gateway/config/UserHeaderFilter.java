package com.bsys.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Pega a autenticação do contexto atual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Verifica se é um Token JWT válido
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();

            // 3. Cria o wrapper para poder adicionar headers
            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);

            // 4. Extrai os dados do JSON do Token e injeta nos headers
            String userId = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            String username = jwt.getClaimAsString("preferred_username");

            if (userId != null) requestWrapper.addHeader("X-User-Id", userId);
            if (email != null) requestWrapper.addHeader("X-User-Email", email);
            if (username != null) requestWrapper.addHeader("X-User-Name", username);

            // 5. Passa a requisição MODIFICADA para frente
            filterChain.doFilter(requestWrapper, response);
        } else {
            // Se não tiver token (anonimo), passa a requisição ORIGINAL
            filterChain.doFilter(request, response);
        }
    }
}
