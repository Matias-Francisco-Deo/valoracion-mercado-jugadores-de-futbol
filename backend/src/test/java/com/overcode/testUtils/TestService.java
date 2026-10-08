package com.overcode.testUtils;

import com.overcode.persistence.repository.dao.jpa.*;
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

    public void eliminarUsuarios() {
        userDAO.deleteAll();

    }

    public void eliminarEquipos() {
        teamDAO.deleteAll();

    }

    public void eliminarDatosJugadores() {
        playerGameDataDAO.deleteAll();

    }

    public void eliminarJugadoresYEquipos() {
        eliminarDatosJugadores();
        playerDAO.deleteAll();
        eliminarEquipos();
    }

    public void eliminarEstrategiasCotizacion() {
        estrategiaCotizacionDAO.deleteAll();
    }

}