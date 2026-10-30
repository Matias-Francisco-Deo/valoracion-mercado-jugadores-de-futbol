package com.overcode.persistence.repository.interfaces;

import com.overcode.model.token.SummaryTokenPlayer;
import com.overcode.model.token.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TokenRepository {

    Optional<Token> findById(Long id);

    void setTokenOwner(Long tokenId, Long newOwnerUserId);

    Page<SummaryTokenPlayer> buscarSummaryTokensPorUsuario(Long userId, String playerName, Pageable pageable);
}
