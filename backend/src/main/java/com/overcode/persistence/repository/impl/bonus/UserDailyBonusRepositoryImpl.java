package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.model.user.User;
import com.overcode.persistence.dto.jpa.bonus.UserDiaryBonusJPADTO;
import com.overcode.persistence.repository.dao.jpa.UserDiaryBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDailyBonusRepositoryImpl implements UserDailyBonusRepository {

    private final UserDiaryBonusDAOJPA userDiaryBonusDAOJPA;

    public UserDailyBonusRepositoryImpl(UserDiaryBonusDAOJPA userDiaryBonusDAOJPA) {
        this.userDiaryBonusDAOJPA = userDiaryBonusDAOJPA;
    }

    @Override
    public UserDailyBonus guardar(UserDailyBonus userDailyBonus) {
        UserDiaryBonusJPADTO dto = UserDiaryBonusJPADTO.desdeModelo(userDailyBonus);
        return userDiaryBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorId(Long id) {
        return userDiaryBonusDAOJPA.findById(id).map(UserDiaryBonusJPADTO::aModelo);
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorUsuario(User user) { // TODO esto se va
        if (user.getId() != null) {
            return recuperarPorUserId(user.getId());
        }
        if (user.getUsername() != null) {
            return userDiaryBonusDAOJPA.findByUserUsername(user.getUsername()).map(UserDiaryBonusJPADTO::aModelo);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorUserId(Long userId) {
        return userDiaryBonusDAOJPA.findByUserId(userId).map(UserDiaryBonusJPADTO::aModelo);
    }

    @Override
    public void eliminarPorId(Long id) {
        userDiaryBonusDAOJPA.deleteById(id);
    }
}
