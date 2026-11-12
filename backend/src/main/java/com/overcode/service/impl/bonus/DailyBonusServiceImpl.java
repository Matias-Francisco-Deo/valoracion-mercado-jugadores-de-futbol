package com.overcode.service.impl.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.mission.BuyDailyBonusMission;
import com.overcode.model.bonus.mission.BuyOrVisitMission;
import com.overcode.model.bonus.mission.Visit3PlayersDailyBonusMission;
import com.overcode.model.player.Player;
import com.overcode.persistence.repository.interfaces.DailyBonusMissionRepository;
import com.overcode.persistence.repository.interfaces.PlayerRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DailyBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DailyBonusServiceImpl implements DailyBonusService {

    private final ConfiguracionMercadoService configuracionMercadoService;
    private final PlayerRepository playerRepository;
    private final DailyBonusMissionRepository dailyBonusMissionRepository;


    public DailyBonusServiceImpl(
            ConfiguracionMercadoService configuracionMercadoService, PlayerRepository playerRepository, DailyBonusMissionRepository dailyBonusMissionRepository) {
        this.configuracionMercadoService = configuracionMercadoService;
        this.playerRepository = playerRepository;
        this.dailyBonusMissionRepository = dailyBonusMissionRepository;
    }

    @Override
    public DailyBonus recuperarSeleccionado() {
        return configuracionMercadoService.recuperar().getDailyBonus();
    }

    @Override
    public BuyOrVisitMission seleccionarNuevaMisionVisitOrBuy() { // TODO testear
        DailyBonus dailyBonus = recuperarSeleccionado();

        BuyOrVisitMission buyOrVisitMission = crearNuevaMisionBuyOrVisit();

        dailyBonus.setRootDailyBonusMission(buyOrVisitMission);
        return buyOrVisitMission;
    }

    @Override
    public BuyOrVisitMission crearNuevaMisionBuyOrVisit() {  // TODO testear

        List<Player> randomPlayers = playerRepository.recuperar3JugadoresAlAzar();

        BuyDailyBonusMission buyMission = new BuyDailyBonusMission();
        Visit3PlayersDailyBonusMission visit3PlayersDailyBonusMission = new Visit3PlayersDailyBonusMission(randomPlayers);

        BuyOrVisitMission buyOrVisitMission = new BuyOrVisitMission(buyMission, visit3PlayersDailyBonusMission);

        dailyBonusMissionRepository.guardar(buyOrVisitMission);

        return buyOrVisitMission;
    }

}
