package com.overcode.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String configuredApiKey;
    private static final String API_KEY_HEADER = "X-API-KEY";

    public ApiKeyAuthFilter(String configuredApiKey) {
        this.configuredApiKey = configuredApiKey;
    }

    /**
     * Registered only on {@code SecurityConfig.adminFilterChain}, scoped via
     * {@code securityMatcher("/api/admin/**")} — every request that reaches this filter is
     * already an admin request, so any new admin endpoint is protected by construction,
     * with no path list to keep in sync here.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

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

        filterChain.doFilter(request, response);
    }
}
