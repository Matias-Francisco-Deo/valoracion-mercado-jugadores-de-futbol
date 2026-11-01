package com.overcode.service.interfaces;

import com.overcode.model.market.PeticionDeposito;
import com.overcode.model.market.transaction.TransaccionFinanciera;
import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.user.Portfolio;
import com.overcode.model.user.User;
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

    User depositarCreditos(PeticionDeposito peticionDeposito);
}
