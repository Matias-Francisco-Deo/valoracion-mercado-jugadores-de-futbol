package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.UserDailyBonusJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDiaryBonusDAOJPA extends JpaRepository<UserDailyBonusJPADTO, Long> {
    Optional<UserDailyBonusJPADTO> findByUserId(Long userId);
    Optional<UserDailyBonusJPADTO> findByUserUsername(String username);
}
