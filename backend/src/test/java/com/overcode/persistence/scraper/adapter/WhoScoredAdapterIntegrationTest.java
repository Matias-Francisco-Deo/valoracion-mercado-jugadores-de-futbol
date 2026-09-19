package com.overcode.persistence.scraper.adapter;

import com.overcode.model.WeeklyMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class WhoScoredAdapterIntegrationTest {

    @Autowired
    private WhoScoredAdapter whoScoredAdapter;

    @Test
    void testEnzoFernandezScraping() {
        System.out.println("Iniciando test de integración real contra WhoScored...");
        System.out.println("Buscando a: Enzo Fernández (Chelsea)... (Playwright está trabajando, dale unos segundos)");
        
        // Ejecutamos la lógica de dominio directo
        WeeklyMetrics metrics = whoScoredAdapter.getPlayerMetrics("chelsea", "enzo fernandez");
        
        // Imprimimos los resultados en la consola para que puedas debuggear
        System.out.println("\n====== DATOS DE ENZO FERNÁNDEZ ======");
        System.out.println("Jugador ID (WhoScored): " + metrics.getPlayerId());
        System.out.println("Partidos Jugados (Apps): " + metrics.getGamesPlayed());
        System.out.println("Goles: " + metrics.getGoals());
        System.out.println("Asistencias: " + metrics.getAssists());
        System.out.println("Tiros al arco: " + metrics.getShotsOnTarget());
        System.out.println("Pases (Efectividad %): " + metrics.getPasses());
        System.out.println("Intercepciones: " + metrics.getInterceptions());
        System.out.println("Quites (Tackles): " + metrics.getTackles());
        System.out.println("Pases Clave: " + metrics.getKeyPasses());
        System.out.println("Rating (Promedio Ponderado): " + metrics.getRating());
        System.out.println("=====================================\n");
        
        // Aserciones de calidad: Le enseñamos al test qué consideramos "éxito"
        assertNotNull(metrics.getPlayerId(), "El ID del jugador no debería ser nulo");
        assertTrue(metrics.getGamesPlayed() > 0, "Debería tener partidos jugados registrados");
        assertTrue(metrics.getRating() > 0.0, "El rating debe ser mayor a 0");
    }
}
