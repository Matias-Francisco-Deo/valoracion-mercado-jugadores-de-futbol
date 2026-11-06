package com.overcode.persistence.repository.impl;

import com.overcode.model.bonus.UserDiaryBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.dto.jpa.bonus.UserDiaryBonusJPADTO;
import com.overcode.persistence.repository.dao.jpa.UserDiaryBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.UserDiaryBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDiaryBonusRepositoryImpl implements UserDiaryBonusRepository {

    private final UserDiaryBonusDAOJPA userDiaryBonusDAOJPA;

    public UserDiaryBonusRepositoryImpl(UserDiaryBonusDAOJPA userDiaryBonusDAOJPA) {
        this.userDiaryBonusDAOJPA = userDiaryBonusDAOJPA;
    }

    @Override
    public UserDiaryBonus guardar(UserDiaryBonus userDiaryBonus) {
        UserDiaryBonusJPADTO dto = UserDiaryBonusJPADTO.desdeModelo(userDiaryBonus);
        return userDiaryBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<UserDiaryBonus> recuperarPorId(Long id) {
        return userDiaryBonusDAOJPA.findById(id).map(UserDiaryBonusJPADTO::aModelo);
    }

    @Override
    public Optional<UserDiaryBonus> recuperarPorUsuario(User user) {
        if (user == null) {
            return Optional.empty();
        }
        if (user.getId() != null) {
            return recuperarPorUserId(user.getId());
        }
        if (user.getUsername() != null) {
            return userDiaryBonusDAOJPA.findByUserUsername(user.getUsername()).map(UserDiaryBonusJPADTO::aModelo);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDiaryBonus> recuperarPorUserId(Long userId) {
        return userDiaryBonusDAOJPA.findByUserId(userId).map(UserDiaryBonusJPADTO::aModelo);
    }

    @Override
    public void eliminarPorId(Long id) {
        userDiaryBonusDAOJPA.deleteById(id);
    }
}
