package com.overcode.config;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.model.bonus.LoginBonusRewards;
import com.overcode.model.cotizacion.ConfiguracionMercado;
import com.overcode.model.cotizacion.EstrategiaCotizacionRating;
import com.overcode.model.cotizacion.EstrategiaCotizacionRendimientoTotal;
import com.overcode.persistence.repository.interfaces.ConfiguracionMercadoRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
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
    private final ConfiguracionMercadoRepository configuracionMercadoRepository;

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService,
            ConfiguracionMercadoService configuracionMercadoService,
            ConfiguracionMercadoRepository configuracionMercadoRepository
    ) {
        this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionMercadoService = configuracionMercadoService;
        this.configuracionMercadoRepository = configuracionMercadoRepository;
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

        ConfiguracionMercado configInicial = new ConfiguracionMercado(1L, new EstrategiaCotizacionRendimientoTotal(), bonusBase);
        ConfiguracionMercado config = configuracionMercadoService.crearConfiguracionSiNoExiste(configInicial);
        if (config.getLoginBonus() == null) {
            config.setLoginBonus(bonusBase);
            configuracionMercadoRepository.guardar(config);
        }
    }

    private void crearEstrategias() {
        estrategiaCotizacionService.guardar(new EstrategiaCotizacionRating());
    }

}
