package com.overcode.testUtils;

import com.overcode.persistence.repository.dao.jpa.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TestService {

    @Autowired
    private UserDAOJPA userDAO;

    @Autowired
    private PlayerDAOJPA playerDAO;

    @Autowired
    private TeamDAOJPA teamDAO;

    @Autowired
    private PlayerGameDataDAOJPA playerGameDataDAO;

    @Autowired
    private EstrategiaCotizacionDAOJPA estrategiaCotizacionDAO;

    @Autowired
    private ConfiguracionCotizacionesDAOJPA configuracionCotizacionesDAO;

    @Autowired
    private RegistroCotizacionDAOJPA registroCotizacionDAO;

    @PersistenceContext
    private EntityManager entityManager;

    public void eliminarUsuarios() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users");
        userDAO.deleteAll();

    }

    public void eliminarEquipos() {
        entityManager.createNativeQuery("DELETE FROM teams");
        teamDAO.deleteAll();

    }

    public void eliminarDatosJugadores() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM players_game_data");
        playerGameDataDAO.deleteAll();

    }

    public void eliminarJugadoresYEquipos() {
        eliminarDatosJugadores();
        eliminarJugadores();
        eliminarEquipos();
    }

    private void eliminarJugadores() {
        entityManager.createNativeQuery("DELETE FROM sell_orders").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM transactions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM players");
        playerDAO.deleteAll();
    }

    public void eliminarCotizaciones() {
        entityManager.createNativeQuery("DELETE FROM registros_cotizaciones");
        registroCotizacionDAO.deleteAll();
        eliminarEstrategiasCotizacion();
    }

    public void eliminarEstrategiasCotizacion() {
        entityManager.createNativeQuery("DELETE FROM configuraciones_cotizaciones");
        configuracionCotizacionesDAO.deleteAll();

        entityManager.createNativeQuery("DELETE FROM estrategias_cotizacion");
        estrategiaCotizacionDAO.deleteAll();
    }

}