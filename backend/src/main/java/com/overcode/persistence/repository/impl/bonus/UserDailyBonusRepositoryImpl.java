package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.persistence.dto.jpa.bonus.UserDailyBonusJPADTO;
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
        UserDailyBonusJPADTO dto = UserDailyBonusJPADTO.desdeModelo(userDailyBonus);
        return userDiaryBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorId(Long id) {
        return userDiaryBonusDAOJPA.findById(id).map(UserDailyBonusJPADTO::aModelo);
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorUserId(Long userId) {
        return userDiaryBonusDAOJPA.findByUserId(userId).map(UserDailyBonusJPADTO::aModelo);
    }

}
