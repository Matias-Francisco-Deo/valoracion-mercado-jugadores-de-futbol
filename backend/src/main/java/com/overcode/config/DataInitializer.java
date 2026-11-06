package com.overcode.config;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.model.bonus.DiaryBonusRewards;
import com.overcode.model.bonus.mission.BuyMission;
import com.overcode.model.bonus.mission.DailyBonusOrMission;
import com.overcode.model.bonus.mission.Mission;
import com.overcode.model.bonus.mission.Visit3PlayersMission;
import com.overcode.model.bonus.mission.VisitPlayerMission;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.model.player.Player;
import com.overcode.model.player.Team;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
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

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService,
            ConfiguracionMercadoService configuracionMercadoService,
            ConfiguracionMercadoRepository configuracionMercadoRepository,
            PlayerRepository playerRepository
    ) {
        this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionMercadoService = configuracionMercadoService;
        this.configuracionMercadoRepository = configuracionMercadoRepository;
        this.playerRepository = playerRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        inicializarConfiguracion();
        crearEstrategias();
        userService.crearSuperusuario();
    }

    private void inicializarConfiguracion() {
        DiaryBonusRewards rewardsBase = new DiaryBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));

        List<Player> players = new ArrayList<>(playerRepository.listarTodos());
        if (players.size() < 3) {
            for (int i = players.size() + 1; i <= 3; i++) {
                Player p = new Player("Jugador Inicial " + i, new Team("Club Inicial " + i, "Liga Inicial"), null);
                p.setCurrentPrice(100.0);
                p = playerRepository.guardar(p);
                players.add(p);
            }
        }

        Collections.shuffle(players);
        Visit3PlayersMission visitMission = new Visit3PlayersMission(
                new VisitPlayerMission(players.get(0)),
                new VisitPlayerMission(players.get(1)),
                new VisitPlayerMission(players.get(2))
        );
        BuyMission buyMission = new BuyMission();
        Mission<?> rootMission = new DailyBonusOrMission(buyMission, visitMission);

        DiaryBonus bonusBase = new DiaryBonus("Bonus Base Semanal", rewardsBase, rootMission);

        ConfiguracionMercado configInicial = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        ConfiguracionMercado config = configuracionMercadoService.crearConfiguracionSiNoExiste(configInicial);
        if (config.getDiaryBonus() == null) {
            config.setDiaryBonus(bonusBase);
            configuracionMercadoRepository.guardar(config);
        }
    }

    private void crearEstrategias() {
        estrategiaCotizacionService.guardar(new EstrategiaCotizacionRating());
    }
}
