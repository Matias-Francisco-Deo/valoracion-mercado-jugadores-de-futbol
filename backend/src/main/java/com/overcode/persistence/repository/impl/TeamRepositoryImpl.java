package com.overcode.persistence.repository.impl;

import com.overcode.model.Team;
import com.overcode.persistence.dto.jpa.TeamJPADTO;
import com.overcode.persistence.repository.dao.jpa.TeamDAOJPA;
import com.overcode.persistence.repository.interfaces.TeamRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamDAOJPA teamDAOJPA;

    public TeamRepositoryImpl(TeamDAOJPA teamDAOJPA) {
        this.teamDAOJPA = teamDAOJPA;
    }

    @Override
    public Team guardar(Team team) {
        TeamJPADTO dto = TeamJPADTO.desdeModelo(team);
        return teamDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<Team> recuperar(Long id) {
        return teamDAOJPA.findById(id).map(TeamJPADTO::aModelo);
    }

    @Override
    public Optional<Team> recuperarPorNombre(String name) {
        return teamDAOJPA.findByName(name).map(TeamJPADTO::aModelo);
    }
}
