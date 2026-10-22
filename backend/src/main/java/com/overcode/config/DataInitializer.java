package com.overcode.config;

import com.overcode.model.bonus.DiaDeLaSemana;
import com.overcode.model.bonus.LoginBonus;
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

import java.util.EnumMap;
import java.util.Map;

@Component
public class DataInitializer {
    private final UserService userService;
    private final EstrategiaCotizacionService estrategiaCotizacionService;
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;
    private final ConfiguracionCotizacionesRepository configuracionCotizacionesRepository;
    private final LoginBonusRepository loginBonusRepository;

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
        this.loginBonusRepository = loginBonusRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        inicializarConfiguracion();
        crearEstrategias();
        userService.crearSuperusuario();
    }

    private void inicializarConfiguracion() {
        LoginBonus bonusBase = loginBonusRepository.recuperarPorNombre("Bonus Base Semanal")
                .orElseGet(() -> {
                    Map<DiaDeLaSemana, Double> rewards = new EnumMap<>(DiaDeLaSemana.class);
                    rewards.put(DiaDeLaSemana.LUNES, 1.0);
                    rewards.put(DiaDeLaSemana.MARTES, 2.0);
                    rewards.put(DiaDeLaSemana.MIERCOLES, 2.0);
                    rewards.put(DiaDeLaSemana.JUEVES, 5.0);
                    rewards.put(DiaDeLaSemana.VIERNES, 5.0);
                    rewards.put(DiaDeLaSemana.SABADO, 10.0);
                    rewards.put(DiaDeLaSemana.DOMINGO, 15.0);
                    return loginBonusRepository.guardar(new LoginBonus("Bonus Base Semanal", rewards));
                });

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
