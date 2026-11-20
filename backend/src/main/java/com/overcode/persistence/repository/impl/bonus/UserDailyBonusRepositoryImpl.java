package com.overcode.persistence.repository.impl.bonus;

import com.overcode.model.bonus.UserDailyBonus;
import com.overcode.persistence.dto.jpa.bonus.UserDailyBonusJPADTO;
import com.overcode.persistence.repository.dao.jpa.UserDailyBonusDAOJPA;
import com.overcode.persistence.repository.interfaces.UserDailyBonusRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDailyBonusRepositoryImpl implements UserDailyBonusRepository {

    private final UserDailyBonusDAOJPA userDailyBonusDAOJPA;

    public UserDailyBonusRepositoryImpl(UserDailyBonusDAOJPA userDailyBonusDAOJPA) {
        this.userDailyBonusDAOJPA = userDailyBonusDAOJPA;
    }

    @Override
    public UserDailyBonus guardar(UserDailyBonus userDailyBonus) {
        UserDailyBonusJPADTO dto = UserDailyBonusJPADTO.desdeModelo(userDailyBonus);
        return userDailyBonusDAOJPA.save(dto).aModelo();
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorId(Long id) {
        return userDailyBonusDAOJPA.findById(id).map(UserDailyBonusJPADTO::aModelo);
    }

    @Override
    public Optional<UserDailyBonus> recuperarPorUserId(Long userId) {
        return userDailyBonusDAOJPA.findByUserId(userId).map(UserDailyBonusJPADTO::aModelo);
    }

}
