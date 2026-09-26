package com.overcode.config.scheduling;

import com.overcode.model.Player;
import com.overcode.service.interfaces.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {

	private final PlayerService playerService;

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    public ScheduledTasks(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Scheduled(cron = "0 0 0 * * MON")
	public void actualizarJugadores() {
		log.info("Actualizando datos de jugadores...");
		List<Player> players = playerService.actualizarDatosJugadores();
		log.info("Se actualizaron {} jugadores", players.size());
	}
}