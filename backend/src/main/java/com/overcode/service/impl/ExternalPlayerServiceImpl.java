package com.overcode.service.impl;

import com.overcode.model.Player;
import com.overcode.persistence.dto.external.PlayerDraftDTO;
import com.overcode.persistence.repository.interfaces.ExternalPlayerRepository;
import com.overcode.service.interfaces.ExternalPlayerService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExternalPlayerServiceImpl implements ExternalPlayerService {

    private final ExternalPlayerRepository externalPlayerRepository;

    public ExternalPlayerServiceImpl(ExternalPlayerRepository externalPlayerRepository) {
        this.externalPlayerRepository = externalPlayerRepository;

    }

    @Override
    public Optional<List<Player>> actualizarJugadores(Integer limit) {
        Optional<List<PlayerDraftDTO>> playerDraftDTOS = externalPlayerRepository.listarJugadores(limit);

        if (playerDraftDTOS.isEmpty()) return Optional.empty();

        List<Player> upsertedPlayers = new java.util.ArrayList<>();
        for (PlayerDraftDTO draftDTO : playerDraftDTOS.get()) {
            Optional<Player> player = externalPlayerRepository.getDatosDeJugador(draftDTO);
            player.ifPresent(value -> upsertedPlayers
                    .add(externalPlayerRepository.upsertPlayerByExternalId(value)));
        }

        if (upsertedPlayers.isEmpty()) return Optional.empty();

        return Optional.of(upsertedPlayers);
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void actualizarJugadoresAsync() {
        System.out.println("Iniciando actualizacion asincrona manual...");
        actualizarJugadores(null);
        System.out.println("Finalizo la actualizacion asincrona manual.");
    }
}
