package com.overcode.config;

import com.overcode.config.scheduling.ScheduledTasks;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
    ScheduledTasksTest.TestSchedulingConfig.class,
    ScheduledTasks.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ScheduledTasksTest {

    @Configuration
    @EnableScheduling
    static class TestSchedulingConfig {

        @Bean
        public ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            ZoneId zone = ZoneId.systemDefault();
            Instant systemNow = Instant.now();
            // Próximo lunes a las 00:00:00
            LocalDate nextMonday = LocalDate.ofInstant(systemNow, zone)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
            LocalDateTime mondayMidnight = nextMonday.atStartOfDay();

            // Seteamos el Clock a 1 segundo antes: Domingo 23:59:59
            LocalDateTime sundayNight = mondayMidnight.minusSeconds(1);
            Instant instant = sundayNight.atZone(zone).toInstant();

            scheduler.setClock(Clock.offset(Clock.system(zone), Duration.between(systemNow, instant)));
            return scheduler;
        }
    }

    @MockitoSpyBean
    ScheduledTasks tasks;

    @MockitoBean
    ExternalPlayerService externalPlayerService;

    @Test
    void actualizarDatosJugadoresSeEjecutaALas12DeLaNoche() {
        when(externalPlayerService.actualizarJugadores(null)).thenReturn(Optional.of(List.of()));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> verify(tasks, atLeastOnce()).actualizarJugadores());
    }

    @Test
    void cuandoSeNoSePuedenActualizarJugadoresNoDaExcepcion() {
        when(externalPlayerService.actualizarJugadores(null)).thenReturn(Optional.empty());

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> verify(tasks, atLeastOnce()).actualizarJugadores());
    }
}
