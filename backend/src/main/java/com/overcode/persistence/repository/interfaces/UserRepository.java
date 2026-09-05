package com.overcode.persistence.repository.interfaces;

import com.overcode.model.User;
import com.overcode.persistence.dto.UserJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface UserRepository {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserJPADTO> findByUsername(String username);

    User save(User user);

    Optional<User> recuperar(Long id);
}
