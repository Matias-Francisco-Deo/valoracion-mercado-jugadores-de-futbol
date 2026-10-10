package com.overcode.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyAuthFilterTest {

    private ApiKeyAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyAuthFilter("super-secret-key");
    }

    @Test
    void debePermitirElPasoSiLaApiKeyEsCorrecta() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-KEY", "super-secret-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus(), "El status debe ser 200 OK");
        assertNull(response.getErrorMessage());
    }

    @Test
    void debeRechazarCon401SiNoHayApiKey() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus(), "Debe retornar 401 Unauthorized");
        assertTrue(response.getContentAsString().contains("Unauthorized: Invalid or missing API Key"));
    }

    @Test
    void debeRechazarCon401SiLaApiKeyEsIncorrecta() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-KEY", "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus(), "Debe retornar 401 Unauthorized");
        assertTrue(response.getContentAsString().contains("Unauthorized: Invalid or missing API Key"));
    }
}
