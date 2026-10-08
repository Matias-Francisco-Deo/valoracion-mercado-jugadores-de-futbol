package com.overcode.persistence.repository;

import com.overcode.persistence.repository.interfaces.EstrategiaCotizacionRepository;
import com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
class EstrategiaCotizacionRepositoryTest {

    @Autowired
    private EstrategiaCotizacionRepository estrategiaCotizacionRepository;

    @Test
    @Transactional
    void guardarFallaCuandoLaEstrategiaNoEstaMapeada() {
        EstrategiaCotizacionSiempre2 estrategia = new EstrategiaCotizacionSiempre2(10.0, 2.0);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> estrategiaCotizacionRepository.guardar(estrategia));
    }

    @Test
    @Transactional
    void recuperarDevuelveVacioCuandoLaEstrategiaNoExiste() {
        assertTrue(estrategiaCotizacionRepository.recuperar(-1L).isEmpty());
    }

    @Test
    @Transactional
    void recuperarTodosDevuelveListaVaciaCuandoNoHayEstrategias() {
        assertTrue(estrategiaCotizacionRepository.recuperarTodos().isEmpty());
    }
}
