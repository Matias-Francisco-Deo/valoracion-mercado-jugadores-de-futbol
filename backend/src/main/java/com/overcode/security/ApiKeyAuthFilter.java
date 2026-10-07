package com.overcode.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${scraper.api.key}")
    private String configuredApiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String ADMIN_PLAYER_PATH_PREFIX = "/api/admin/players";
    private static final String ADMIN_SCRAPER_PATH_PREFIX = "/api/admin/players";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean protectedRoute = path.startsWith(ADMIN_PLAYER_PATH_PREFIX) || path.startsWith(ADMIN_SCRAPER_PATH_PREFIX);

        if (protectedRoute) {
            String requestApiKey = request.getHeader(API_KEY_HEADER);
            String expectedApiKey = configuredApiKey == null ? "" : configuredApiKey.trim();
            String providedApiKey = requestApiKey == null ? "" : requestApiKey.trim();

            if (providedApiKey.isEmpty() || !providedApiKey.equals(expectedApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain");
                response.getWriter().write("Unauthorized: Invalid or missing API Key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
