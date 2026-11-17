package com.overcode.aspect;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditAspectTest {

    private AuditAspect auditAspect;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        auditAspect = new AuditAspect();
        logger = (Logger) LoggerFactory.getLogger(AuditAspect.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        SecurityContextHolder.clearContext();
    }

    @Test
    void audit_whenUserAuthenticated_shouldLogFormattedAuditWithUsername() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", "password", Collections.emptyList())
        );

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("UserController.getUser(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn("OK");

        Object result = auditAspect.audit(joinPoint);

        assertEquals("OK", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.matches("<[^,]+, user123, UserController\\.getUser\\(\\.\\.\\), \\[1\\], \\d+ms>"),
                "Log message should match expected format: " + logMessage);
    }

    @Test
    void audit_whenAuthenticationNull_shouldLogFormattedAuditWithAnonymous() throws Throwable {
        SecurityContextHolder.clearContext();

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("AuthController.login(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"loginDto"});
        when(joinPoint.proceed()).thenReturn("Token");

        Object result = auditAspect.audit(joinPoint);

        assertEquals("Token", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.matches("<[^,]+, ANONYMOUS, AuthController\\.login\\(\\.\\.\\), \\[loginDto\\], \\d+ms>"),
                "Log message should match expected format: " + logMessage);
    }

    @Test
    void audit_whenUserIsAnonymousUser_shouldLogFormattedAuditWithAnonymous() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null)
        );

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("PlayerController.getPlayers(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenReturn("List");

        Object result = auditAspect.audit(joinPoint);

        assertEquals("List", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.matches("<[^,]+, ANONYMOUS, PlayerController\\.getPlayers\\(\\.\\.\\), \\[\\]\\, \\d+ms>"),
                "Log message should match expected format: " + logMessage);
    }

    @Test
    void audit_whenNotAuthenticated_shouldLogFormattedAuditWithAnonymous() throws Throwable {
        UsernamePasswordAuthenticationToken unauthenticated = new UsernamePasswordAuthenticationToken("someUser", "pass");
        unauthenticated.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(unauthenticated);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("PlayerController.getPlayers(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10});
        when(joinPoint.proceed()).thenReturn("List");

        Object result = auditAspect.audit(joinPoint);

        assertEquals("List", result);
        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.matches("<[^,]+, ANONYMOUS, PlayerController\\.getPlayers\\(\\.\\.\\), \\[10\\], \\d+ms>"),
                "Log message should match expected format: " + logMessage);
    }

    @Test
    void audit_whenExceptionThrown_shouldLogAndRethrowException() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user456", "password", Collections.emptyList())
        );

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("UserController.delete(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{99L});
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(joinPoint.proceed()).thenThrow(expectedException);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> auditAspect.audit(joinPoint));
        assertSame(expectedException, actualException);

        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        String logMessage = event.getFormattedMessage();
        assertTrue(logMessage.matches("<[^,]+, user456, UserController\\.delete\\(\\.\\.\\), \\[99\\], \\d+ms>"),
                "Log message should match expected format: " + logMessage);
    }
}
