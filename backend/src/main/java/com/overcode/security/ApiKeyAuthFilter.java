package com.overcode.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${scraper.api.key}")
    private String configuredApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String ADMIN_PLAYER_PATH_PREFIX = "/api/admin/players";
    private static final String ADMIN_ESTRATEGIAS_PATH_PREFIX = "/api/admin/quotes";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean protectedRoute = path.startsWith(ADMIN_PLAYER_PATH_PREFIX) || path.startsWith(ADMIN_ESTRATEGIAS_PATH_PREFIX);

        if (protectedRoute) {
            String requestApiKey = request.getHeader(API_KEY_HEADER);
            String expectedApiKey = configuredApiKey == null ? "" : configuredApiKey.trim();
            String providedApiKey = requestApiKey == null ? "" : requestApiKey.trim();

            if (providedApiKey.isEmpty() || !providedApiKey.equals(expectedApiKey)) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain");
                response.getWriter().write("Unauthorized: Invalid or missing API Key");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "api-key-user",
                    null,
                    List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
