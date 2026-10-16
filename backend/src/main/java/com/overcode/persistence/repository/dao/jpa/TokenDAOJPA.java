package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.TokenJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenDAOJPA extends JpaRepository<TokenJPADTO, Long> {

    List<TokenJPADTO> findByOwnerIdAndPlayerId(Long ownerId, Long playerId);

    @Modifying
    @Query("UPDATE token t SET t.owner.id = :newOwnerId WHERE t.id = :tokenId")
    void updateOwnerId(@Param("tokenId") Long tokenId, @Param("newOwnerId") Long newOwnerId);

    @Modifying
    @Query("UPDATE token t SET t.owner = null WHERE t.id = :tokenId")
    void clearOwner(@Param("tokenId") Long tokenId);
}
