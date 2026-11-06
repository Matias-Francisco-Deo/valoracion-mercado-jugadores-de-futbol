package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.bonus.UserDiaryBonusJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDiaryBonusDAOJPA extends JpaRepository<UserDiaryBonusJPADTO, Long> {
    Optional<UserDiaryBonusJPADTO> findByUserId(Long userId);
    Optional<UserDiaryBonusJPADTO> findByUserUsername(String username);
}
