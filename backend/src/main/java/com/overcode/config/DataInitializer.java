package com.overcode.config;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import com.overcode.model.cotizacion.EstrategiaCotizacionUno;
import com.overcode.service.interfaces.ConfiguracionCotizacionesService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.service.interfaces.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {
    private final UserService userService;
    private final EstrategiaCotizacionService estrategiaCotizacionService;
    private final ConfiguracionCotizacionesService configuracionCotizacionesService;

    public DataInitializer(
            UserService userService,
            EstrategiaCotizacionService estrategiaCotizacionService, ConfiguracionCotizacionesService configuracionCotizacionesService
            ) {this.userService = userService;
        this.estrategiaCotizacionService = estrategiaCotizacionService;
        this.configuracionCotizacionesService = configuracionCotizacionesService;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        inicializarConfiguracion();
        crearEstrategias();
        userService.crearSuperusuario();
    }

    private void inicializarConfiguracion() {
        ConfiguracionCotizaciones configInicial = new ConfiguracionCotizaciones(new EstrategiaCotizacionUno(3.0, 1.0));
        configuracionCotizacionesService.crearConfiguracionSiNoExiste(configInicial);
    }

    private void crearEstrategias() {
        estrategiaCotizacionService.guardar(new EstrategiaCotizacionUno(5.0, 1.0));

    }


}
