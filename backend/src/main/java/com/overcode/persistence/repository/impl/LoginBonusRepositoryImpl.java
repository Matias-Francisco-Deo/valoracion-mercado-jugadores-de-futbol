package com.overcode.persistence.repository.impl;

import com.overcode.model.bonus.LoginBonus;
import com.overcode.persistence.dto.jpa.bonus.LoginBonusJPADTO;
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
       LoginBonusJPADTO dto = LoginBonusJPADTO.desdeModelo(loginBonus);
       return loginBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<LoginBonus> recuperarPorId(Long id) {
     return loginBonusDAOJPA.findById(id).map(LoginBonusJPADTO::aModelo);
    }

    @Override
    public Optional<LoginBonus> recuperarPorNombre(String nombre) {
        return loginBonusDAOJPA.findByNombre(nombre).map(LoginBonusJPADTO::aModelo);

    }

    @Override
    public boolean existePorId(Long id) {
        return loginBonusDAOJPA.existsById(id);

    }

    @Override
    public boolean existePorNombre(String nombre) {

        return loginBonusDAOJPA.existsByNombre(nombre);

    }

    @Override
    public List<LoginBonus> recuperarTodos() {
            return loginBonusDAOJPA.findAll().stream().map(LoginBonusJPADTO::aModelo).toList();
    }
}
