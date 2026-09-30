package com.overcode.config.scheduling;

import com.overcode.model.Player;
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
	private static final Integer MAX_PLAYERS_TO_UPDATE = null;

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    public ScheduledTasks(ExternalPlayerService externalPlayerService) {
        this.externalPlayerService = externalPlayerService;
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
	}
}