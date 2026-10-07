package com.overcode.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyAuthFilterTest {

    private ApiKeyAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyAuthFilter();
        ReflectionTestUtils.setField(filter, "configuredApiKey", "clave-valida");
    }

    @Test
    void rechazaRutaAdminSinApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/players/actualizar-jugadores");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> fail("No se debe continuar el chain cuando falta la API key");

        filter.doFilterInternal(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }

    @Test
    void permiteRutaAdminConApiKeyValida() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/admin/players/actualizar-jugadores");
        request.addHeader("X-API-KEY", "clave-valida");
        MockHttpServletResponse response = new MockHttpServletResponse();
        final boolean[] continued = {false};

        filter.doFilterInternal(request, response, (req, res) -> continued[0] = true);

        assertTrue(continued[0]);
        assertEquals(200, response.getStatus());
    }

    @Test
    void permiteRutasNoProtegidasSinApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        final boolean[] continued = {false};

        filter.doFilterInternal(request, response, (req, res) -> continued[0] = true);

        assertTrue(continued[0]);
    }
}
