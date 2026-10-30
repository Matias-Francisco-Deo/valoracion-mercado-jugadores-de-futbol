package com.overcode.service.impl;

import com.overcode.model.market.MotorDeMercado;
import com.overcode.model.market.orden.OrdenDeVenta;
import com.overcode.model.player.Player;
import com.overcode.model.user.User;
import com.overcode.persistence.repository.interfaces.OrdenDeCompraRepository;
import com.overcode.persistence.repository.interfaces.UserRepository;
import com.overcode.service.exception.EntidadNoEncontradaException;
import com.overcode.service.interfaces.TokenEmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TokenEmissionServiceImpl implements TokenEmissionService {

    private final UserRepository userRepository;
    private final OrdenDeCompraRepository ordenDeCompraRepository;

    @Value("${superuser.email:overcode@gmail.com}")
    private String superuserEmail;

    public TokenEmissionServiceImpl(UserRepository userRepository, OrdenDeCompraRepository ordenDeCompraRepository) {
        this.userRepository = userRepository;
        this.ordenDeCompraRepository = ordenDeCompraRepository;
    }

    @Override
    public void emitTokensForNewPlayers(List<Player> players) {
        if (players.isEmpty()) return;

        User superuser = userRepository.findByEmail(superuserEmail)
                .orElseThrow(() -> new EntidadNoEncontradaException("Superuser not found with email: " + superuserEmail));

        for (Player player : players) {

            MotorDeMercado motorDeMercado = new MotorDeMercado();
            List<OrdenDeVenta> orders = motorDeMercado.publicarJugador(player, superuser);

            ordenDeCompraRepository.saveAll(orders);
        }
        userRepository.guardar(superuser);
    }

    @Override
    public void emitTokensForNewPlayer(Player player) {
        emitTokensForNewPlayers(List.of(player));
    }
}
