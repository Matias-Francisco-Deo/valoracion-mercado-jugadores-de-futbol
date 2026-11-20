package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.DailyBonus;
import com.overcode.persistence.dto.jpa.bonus.DailyBonusJPADTO;
import com.overcode.persistence.repository.dao.jpa.DailyBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.DailyBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DailyBonusRepositoryImpl implements DailyBonusRepository {

    private final DailyBonusDAOJPA dailyBonusDAOJPA;

    public DailyBonusRepositoryImpl(DailyBonusDAOJPA dailyBonusDAOJPA) {
        this.dailyBonusDAOJPA = dailyBonusDAOJPA;
    }

    @Override
    public DailyBonus guardar(DailyBonus dailyBonus) {
        DailyBonusJPADTO dto = DailyBonusJPADTO.desdeModelo(dailyBonus);
        return dailyBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<DailyBonus> recuperarPorId(Long id) {
        return dailyBonusDAOJPA.findById(id).map(DailyBonusJPADTO::aModelo);
    }

    @Override
    public Optional<DailyBonus> recuperarPorNombre(String nombre) {
        return dailyBonusDAOJPA.findByNombre(nombre).map(DailyBonusJPADTO::aModelo);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return dailyBonusDAOJPA.existsByNombre(nombre);
    }

    @Override
    public List<DailyBonus> recuperarTodos() {
        return dailyBonusDAOJPA.findAll().stream().map(DailyBonusJPADTO::aModelo).toList();
    }
}
