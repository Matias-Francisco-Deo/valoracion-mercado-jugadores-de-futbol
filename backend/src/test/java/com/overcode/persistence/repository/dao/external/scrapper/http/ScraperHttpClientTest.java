package com.overcode.persistence.repository.dao.external.scrapper.http;

import com.overcode.persistence.repository.dao.external.scrapper.exception.ScraperExtractionException;
import com.overcode.persistence.repository.dao.external.scrapper.whoscored.WhoScoredTestFixtures;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ScraperHttpClientTest {

    private static MockWebServer mockWebServer;
    private static ScraperHttpClient client;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        client = new ScraperHttpClient();
        client.init();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        if (client != null) {
            client.cleanup();
        }
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }
}
