package com.overcode.config;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.model.bonus.DailyBonusRewards;
import com.overcode.model.bonus.mission.DailyBonusMission;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.DailyBonusService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {
    private final UserService userService;
    private final EstrategiaCotizacionService estrategiaCotizacionService;
    private final ConfiguracionMercadoService configuracionMercadoService;
    private final DailyBonusService dailyBonusService;

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService,
            ConfiguracionMercadoService configuracionMercadoService,
            DailyBonusService dailyBonusService) {
        this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionMercadoService = configuracionMercadoService;
        this.dailyBonusService = dailyBonusService;
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

        DailyBonusMission<?> baseMision = dailyBonusService.crearNuevaMision();

        DailyBonus bonusBase = new DailyBonus("Bonus Base Semanal", rewardsBase, baseMision);

        ConfiguracionMercado configInicial = new ConfiguracionMercado(new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        configuracionMercadoService.crearConfiguracionSiNoExiste(configInicial);
    }

    private void crearEstrategias() {
        boolean yaSeGeneroEstrategiaConEseTipo = estrategiaCotizacionService.recuperarTodos().stream()
                .anyMatch((estrategiaCotizacion -> estrategiaCotizacion.getClass() == EstrategiaCotizacionRating.class));
        if (yaSeGeneroEstrategiaConEseTipo) return;

        estrategiaCotizacionService.guardar(new EstrategiaCotizacionRating());
    }
}
