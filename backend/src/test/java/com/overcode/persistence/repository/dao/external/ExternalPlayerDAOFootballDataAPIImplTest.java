package com.overcode.persistence.repository.dao.external;

import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionAreaDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.CompetitionDTO;
import com.overcode.persistence.dto.external.FootballDataAPI.TeamDraftDTO;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExternalPlayerDAOFootballDataAPIImplTest {

    public static final CompetitionDTO COMPETITION_1 = new CompetitionDTO(2014L, "Primera Division", new CompetitionAreaDTO("Spain"));

    @Autowired
    private ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImpl;

    private static MockWebServer mockWebServer;
    private ExternalPlayerDAOFootballDataAPIImpl externalPlayerDAOFootballDataAPIImplMock;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        // Point the DAO to the mock server's dynamically assigned URL
        externalPlayerDAOFootballDataAPIImplMock = new ExternalPlayerDAOFootballDataAPIImpl("mockKey", mockWebServer.url("/").toString());
    }

    @Test
    void encuentraTodasLasLigasConIdsMock() {
        String json = """
                {                                                                                                                                                                                                                \s
                    "competitions": [                                                                                                                                                                                            \s
                        { "id": 2014, "name": "Primera Division", "area": { "name": "Spain" } },                                                                                                                                 \s
                        { "id": 2015, "name": "Ligue 1", "area": { "name": "France" } },                                                                                                                                         \s
                        { "id": 2021, "name": "Premier League", "area": { "name": "England" } },                                                                                                                                 \s
                        { "id": 2002, "name": "Bundesliga", "area": { "name": "Germany" } },                                                                                                                                     \s
                        { "id": 2019, "name": "Serie A", "area": { "name": "Italy" } }                                                                                                                                           \s
                    ]                                                                                                                                                                                                            \s
                }                                                                                                                                                                                                                \s
               \s""";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(json));

        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.getCompetitions();

        assertTrue(ligas.isPresent());
        assertEquals(5, ligas.get().size());
    }

    @Test
    void encuentraTodosLosEquiposDeUnaCompetenciaMock() throws InterruptedException {
        String json = """
                {                                                                                                                                                                                                                \s
                    "teams": [                                                                                                                                                                                                   \s
                        {                                                                                                                                                                                                        \s
                            "id": 81,                                                                                                                                                                                            \s
                            "name": "FC Barcelona",                                                                                                                                                                              \s
                            "squad": [                                                                                                                                                                                           \s
                                { "id": 1, "name": "Lamine Yamal" },                                                                                                                                                             \s
                                { "id": 2, "name": "Robert Lewandowski" }                                                                                                                                                        \s
                            ]                                                                                                                                                                                                    \s
                        }                                                                                                                                                                                                        \s
                    ]                                                                                                                                                                                                            \s
                }                                                                                                                                                                                                                \s
               \s""";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(json));

        Optional<List<TeamDraftDTO>> equipos = externalPlayerDAOFootballDataAPIImplMock.getTeamsOfCompetition(COMPETITION_1);

        assertTrue(equipos.isPresent());
        assertFalse(equipos.get().isEmpty());
        assertEquals("FC Barcelona", equipos.get().get(0).name());
    }

    @Test
    void encuentraTodosLosJugadoresMock() {
        // listarJugadores() makes multiple calls: 1 to /competitions and 1 per competition to /competitions/{id}/teams
        // Use a Dispatcher to handle different endpoints:

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody("""
                                    {                                                                                                                                                                                            \s
                                        "competitions": [                                                                                                                                                                        \s
                                            { "id": 2014, "name": "Primera Division", "area": { "name": "Spain" } }                                                                                                              \s
                                        ]                                                                                                                                                                                        \s
                                    }                                                                                                                                                                                            \s
                               \s"""));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody("""
                                    {                                                                                                                                                                                            \s
                                        "teams": [                                                                                                                                                                               \s
                                            {                                                                                                                                                                                    \s
                                                "id": 81,                                                                                                                                                                        \s
                                                "name": "FC Barcelona",                                                                                                                                                          \s
                                                "squad": [                                                                                                                                                                       \s
                                                    { "id": 101, "name": "Pedri" }                                                                                                                                               \s
                                                ]                                                                                                                                                                                \s
                                            }                                                                                                                                                                                    \s
                                        ]                                                                                                                                                                                        \s
                                    }                                                                                                                                                                                            \s
                               \s"""));


        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImplMock.listarJugadores();

        assertTrue(jugadores.isPresent());
        assertFalse(jugadores.get().isEmpty());
        assertEquals("Pedri", jugadores.get().get(0).name());
    }


    @Disabled("Use manually since it can fail if the API is down")
    @Test
    void encuentraTodosLosJugadores() throws InterruptedException {
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

        assertFalse(jugadores.get().isEmpty());
    }

    @Disabled("Use manually since it can fail if the API is down")
    @Test
    void encuentraTodosLosJugadoresConDatos() throws InterruptedException {
        Optional<List<PlayerDraftDTO>> jugadores = externalPlayerDAOFootballDataAPIImpl.listarJugadores();

        jugadores.get().forEach(jugador -> {
            assertNotNull(jugador.name());
            assertNotNull(jugador.league());
        });
        assertFalse(jugadores.get().isEmpty());
    }

    @Disabled("Use manually since it can fail if the API is down")
    @Test
    void encuentraTodasLasLigasConIds() throws InterruptedException {
        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImpl.getCompetitions();

        ligas.get().forEach(liga -> {
            assertNotNull(liga.id());
            assertNotNull(liga.name());
        });
        assertEquals(externalPlayerDAOFootballDataAPIImpl.getLeaguesToUse().size(), ligas.get().size());
    }

    @Disabled("Use manually since it can fail if the API is down")
    @Test
    void encuentraTodosLosEquiposDeUnaCompetencia() throws InterruptedException {
        Optional<List<TeamDraftDTO>> equipos = externalPlayerDAOFootballDataAPIImpl.getTeamsOfCompetition(COMPETITION_1);

        equipos.get().forEach(equipo -> {
            assertNotNull(equipo.id());
            assertNotNull(equipo.name());
        });
        assertFalse(equipos.get().isEmpty());
    }

    @Test
    void noEncuentraLigasPorFalloDeApiEntoncesDaEmpty() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        Optional<List<CompetitionDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.getCompetitions();

        assertTrue(ligas.isEmpty());
    }

    @Test
    void noEncuentraJugadoresPorFalloDeApiEntoncesDaEmpty() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        Optional<List<PlayerDraftDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.listarJugadores();

        assertTrue(ligas.isEmpty());
    }

    @Test
    void noEncuentraJugadoresDeCompetenciasPorFalloDeApiEntoncesDaEmpty() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        Optional<List<TeamDraftDTO>> ligas = externalPlayerDAOFootballDataAPIImplMock.getTeamsOfCompetition(COMPETITION_1);

        assertTrue(ligas.isEmpty());
    }



    }



