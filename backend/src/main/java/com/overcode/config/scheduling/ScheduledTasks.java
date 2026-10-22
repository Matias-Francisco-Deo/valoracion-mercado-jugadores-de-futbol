package com.overcode.config.scheduling;

import com.overcode.model.player.Player;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScheduledTasks {

	private final ExternalPlayerService externalPlayerService;
	private final EstrategiaCotizacionService estrategiaCotizacionService;
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final Integer MAX_PLAYERS_TO_UPDATE = null;

	public ScheduledTasks(ExternalPlayerService externalPlayerService, EstrategiaCotizacionService estrategiaCotizacionService) {
        this.externalPlayerService = externalPlayerService;
		this.estrategiaCotizacionService = estrategiaCotizacionService;
	}

    @Scheduled(cron = "0 0 0 * * MON")
	public void actualizarJugadores() {
		log.info("Actualizando datos de jugadores...");
		Optional<List<Player>> players = externalPlayerService.actualizarJugadores(MAX_PLAYERS_TO_UPDATE);
		if (players.isPresent()) {
			log.info("Se actualizaron {} jugadores", players.get().size());
		} else {
			log.warn("No se actualizaron jugadores");
		}
		estrategiaCotizacionService.cotizarJugadores();
		log.info("Se cotizaron todos los jugadores");
	}
}