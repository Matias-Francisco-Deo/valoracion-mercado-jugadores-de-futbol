package com.overcode.service.interfaces;

import com.overcode.controller.dto.market.DepositPetition;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.user.Portfolio;
import com.overcode.model.user.User;
import com.overcode.model.venta.transaction.TransaccionFinanciera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    User guardar(User user);

    User recuperar(Long id);

    User crearSuperusuario();

    Optional<User> recuperarPorEmail(String email);

    Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable);

    Page<TransaccionFinanciera> recuperarTransaccionesDeUnUser(Long userId, Pageable pageable);

    Portfolio recuperarPortfolioDeUser(User user);

    User depositarCreditos(DepositPetition depositPetition);
}
