package com.overcode.persistence.repository.interfaces;

import com.overcode.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {

    Optional<Token> findById(Long id);

    List<Token> findByOwnerIdAndPlayerId(Long ownerId, Long playerId);

    void setTokenOwner(Long tokenId, Long newOwnerUserId);

    void clearTokenOwner(Long tokenId);
}
