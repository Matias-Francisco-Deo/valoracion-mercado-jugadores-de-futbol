package com.overcode.persistence.repository.impl;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.persistence.dto.jpa.bonus.LoginBonusJPADTO;
import com.overcode.persistence.exception.LoginBonusPersistenciaException;
import com.overcode.persistence.repository.dao.jpa.LoginBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.LoginBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LoginBonusRepositoryImpl implements LoginBonusRepository {

    private final LoginBonusDAOJPA loginBonusDAOJPA;

    public LoginBonusRepositoryImpl(LoginBonusDAOJPA loginBonusDAOJPA) {
        this.loginBonusDAOJPA = loginBonusDAOJPA;
    }

    @Override
    public LoginBonus guardar(LoginBonus loginBonus) {
        try {
            if (loginBonus == null) {
                throw new LoginBonusPersistenciaException("No se puede guardar un LoginBonus nulo.");
            }
            LoginBonusJPADTO dto = LoginBonusJPADTO.desdeModelo(loginBonus);
            return loginBonusDAOJPA.save(dto).aModelo();
        } catch (LoginBonusPersistenciaException e) {
            throw e;
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al guardar el LoginBonus en la base de datos.", e);
        }
    }

    @Override
    public Optional<LoginBonus> recuperarPorId(Long id) {
        try {
            return loginBonusDAOJPA.findById(id).map(LoginBonusJPADTO::aModelo);
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al recuperar el LoginBonus con id: " + id, e);
        }
    }

    @Override
    public Optional<LoginBonus> recuperarPorNombre(String nombre) {
        try {
            return loginBonusDAOJPA.findByNombre(nombre).map(LoginBonusJPADTO::aModelo);
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al recuperar el LoginBonus con nombre: " + nombre, e);
        }
    }

    @Override
    public boolean existePorId(Long id) {
        try {
            return loginBonusDAOJPA.existsById(id);
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al verificar existencia de LoginBonus con id: " + id, e);
        }
    }

    @Override
    public boolean existePorNombre(String nombre) {
        try {
            return loginBonusDAOJPA.existsByNombre(nombre);
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al verificar existencia de LoginBonus con nombre: " + nombre, e);
        }
    }

    @Override
    public List<LoginBonus> recuperarTodos() {
        try {
            return loginBonusDAOJPA.findAll().stream().map(LoginBonusJPADTO::aModelo).toList();
        } catch (Exception e) {
            throw new LoginBonusPersistenciaException("Error al recuperar todos los LoginBonus.", e);
        }
    }
}
