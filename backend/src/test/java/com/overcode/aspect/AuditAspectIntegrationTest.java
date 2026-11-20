package com.overcode.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuditAspectIntegrationTest.TestConfig.class)
class AuditAspectIntegrationTest {

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public AuditAspect auditAspect() {
            return new AuditAspect();
        }

        @Bean
        public SampleController sampleController() {
            return new SampleController();
        }
    }

    @RestController
    static class SampleController {
        @GetMapping("/test/{id}")
        public String getSample(@PathVariable Long id) {
            return "sample-" + id;
        }
    }

    @Autowired
    private SampleController sampleController;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(AuditAspect.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @WithMockUser(username = "springUser")
    void shouldInterceptRestControllerMethodWhenInvoked() {
        String result = sampleController.getSample(42L);

        assertEquals("sample-42", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.contains("springUser"));
        assertTrue(logMessage.contains("getSample(..)"));
        assertTrue(logMessage.contains("[42]"));
        assertTrue(logMessage.startsWith("<"));
        assertTrue(logMessage.endsWith("ms>"));
    }
}
