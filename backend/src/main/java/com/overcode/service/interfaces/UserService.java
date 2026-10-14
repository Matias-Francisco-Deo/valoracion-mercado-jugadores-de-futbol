package com.overcode.service.interfaces;

import com.overcode.controller.dto.user.PortfolioDataDTO;
import com.overcode.model.User;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User guardar(User user);

    User recuperar(Long id);

    User crearSuperusuario();

    Optional<User> recuperarPorEmail(String email);

    PortfolioDataDTO recuperarPortfolio(Long userId, Pageable pageable);

}
