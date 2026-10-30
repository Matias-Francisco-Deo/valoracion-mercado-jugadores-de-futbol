package com.overcode.config;

import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.interfaces.ConfiguracionMercadoService;
import com.overcode.service.interfaces.EstrategiaCotizacionService;
import com.overcode.testUtils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static junit.framework.TestCase.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
class DataInitializerTest {

    @Autowired
    private DataInitializer dataInitializer;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EstrategiaCotizacionService estrategiaCotizacionService;

    @Autowired
    private ConfiguracionMercadoService configuracionMercadoService;

    @Autowired
    private TestService testService;

    @BeforeEach
    void setUp() {
        testService.eliminarUsuarios();
    }

    @Test
    void alInvocarInitializeSeGeneraElSuperusuario() {
        assertTrue(userRepository.findByUsername("superuser").isEmpty());

        dataInitializer.initialize();

        assertTrue(userRepository.findByUsername("superuser").isPresent());
    }

    @Test
    void alInvocarInitializeSeGeneraConfiguracion() {
        dataInitializer.initialize();

        assertDoesNotThrow(() -> configuracionMercadoService.recuperar());
    }

    @Test
    void alInvocarInitializeSeGeneranEstrategias() {
        dataInitializer.initialize();

        assertFalse(estrategiaCotizacionService.recuperarTodos().isEmpty());
    }

    @AfterEach
    void tearDown() {
        testService.eliminarUsuarios();
    }
}
