package com.overcode.config;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.DailyBonusRewards;
import com.overcode.model.bonus.mission.*;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer {
    private final UserService userService;
    private final EstrategiaCotizacionService estrategiaCotizacionService;
    private final ConfiguracionMercadoService configuracionMercadoService;
    private final ConfiguracionMercadoRepository configuracionMercadoRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService,
            ConfiguracionMercadoService configuracionMercadoService,
            ConfiguracionMercadoRepository configuracionMercadoRepository,
            PlayerRepository playerRepository,
            TeamRepository teamRepository
    ) {
        this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionMercadoService = configuracionMercadoService;
        this.configuracionMercadoRepository = configuracionMercadoRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        inicializarConfiguracion();
        crearEstrategias();
        userService.crearSuperusuario();
    }

    private void inicializarConfiguracion() {
        DailyBonusRewards rewardsBase = new DailyBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));

        List<Player> players = new ArrayList<>(playerRepository.listarTodos());
        if (players.size() < 3) {
            for (int i = players.size() + 1; i <= 3; i++) {
                String clubName = "Club Inicial " + i;
                String leagueName = "Liga Inicial";
                Team team = teamRepository.recuperarPorNombreYLiga(clubName, leagueName)
                        .orElseGet(() -> teamRepository.guardar(new Team(clubName, leagueName)));
                Player p = new Player("Jugador Inicial " + i, team, null);
                p.setCurrentPrice(100.0);
                p = playerRepository.guardar(p);
                players.add(p);
            }
        }

        Collections.shuffle(players);
        Visit3PlayersDailyBonusMission visitMission = new Visit3PlayersDailyBonusMission(
                new VisitPlayerDailyBonusMission(players.get(0)),
                new VisitPlayerDailyBonusMission(players.get(1)),
                new VisitPlayerDailyBonusMission(players.get(2))
        );
        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        DailyBonusMission<?> rootDailyBonusMission = new BuyOrVisitMission(buyMission, visitMission);

        DailyBonus bonusBase = new DailyBonus("Bonus Base Semanal", rewardsBase, rootDailyBonusMission);

        ConfiguracionMercado configInicial = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        ConfiguracionMercado config = configuracionMercadoService.crearConfiguracionSiNoExiste(configInicial);
        if (config.getDailyBonus() == null) {
            config.setDailyBonus(bonusBase);
            configuracionMercadoRepository.guardar(config);
        }
    }

    private void crearEstrategias() {
        estrategiaCotizacionService.guardar(new EstrategiaCotizacionRating());
    }
}
