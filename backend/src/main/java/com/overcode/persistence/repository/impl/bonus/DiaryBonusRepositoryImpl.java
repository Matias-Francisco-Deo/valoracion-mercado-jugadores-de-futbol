package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.DiaryBonus;
import com.overcode.persistence.dto.jpa.bonus.DiaryBonusJPADTO;
import com.overcode.persistence.repository.dao.jpa.DiaryBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.DiaryBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DiaryBonusRepositoryImpl implements DiaryBonusRepository {

    private final DiaryBonusDAOJPA diaryBonusDAOJPA;

    public DiaryBonusRepositoryImpl(DiaryBonusDAOJPA diaryBonusDAOJPA) {
        this.diaryBonusDAOJPA = diaryBonusDAOJPA;
    }

    @Override
    public DiaryBonus guardar(DiaryBonus diaryBonus) {
        DiaryBonusJPADTO dto = DiaryBonusJPADTO.desdeModelo(diaryBonus);
        return diaryBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<DiaryBonus> recuperarPorId(Long id) {
        return diaryBonusDAOJPA.findById(id).map(DiaryBonusJPADTO::aModelo);
    }

    @Override
    public Optional<DiaryBonus> recuperarPorNombre(String nombre) {
        return diaryBonusDAOJPA.findByNombre(nombre).map(DiaryBonusJPADTO::aModelo);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return diaryBonusDAOJPA.existsByNombre(nombre);
    }

    @Override
    public List<DiaryBonus> recuperarTodos() {
        return diaryBonusDAOJPA.findAll().stream().map(DiaryBonusJPADTO::aModelo).toList();
    }
}
