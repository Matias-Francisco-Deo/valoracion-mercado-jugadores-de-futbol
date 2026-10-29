package com.overcode.config;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.LoginBonusRewards;
import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.persistence.repository.interfaces.ConfiguracionCotizacionesRepository;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
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
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;
    private final ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService,
            ConfiguracionCotizacionesService configuracionCotizacionesService,
            ConfiguracionCotizacionesRepository configuracionCotizacionesRepository,
            LoginBonusRepository loginBonusRepository
    ) {
        this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionCotizacionesService = configuracionCotizacionesService;
        this.configuracionCotizacionesRepository = configuracionCotizacionesRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        inicializarConfiguracion();
        crearEstrategias();
        userService.crearSuperusuario();
    }

    private void inicializarConfiguracion() {
        LoginBonusRewards rewardsBase = new LoginBonusRewards(List.of(1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0));
        LoginBonus bonusBase = new LoginBonus("Bonus login simple", rewardsBase);

        ConfiguracionCotizaciones configInicial = new ConfiguracionCotizaciones(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        ConfiguracionCotizaciones config = configuracionCotizacionesService.crearConfiguracionSiNoExiste(configInicial);
        if (config.getLoginBonus() == null) {
            config.setLoginBonus(bonusBase);
            configuracionCotizacionesRepository.guardar(config);
        }
    }

    private void crearEstrategias() {
        estrategiaCotizacionService.guardar(new EstrategiaCotizacionRating());
    }

}
