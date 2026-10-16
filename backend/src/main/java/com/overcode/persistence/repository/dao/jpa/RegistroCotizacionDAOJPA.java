package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.RegistroCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroCotizacionDAOJPA extends JpaRepository<RegistroCotizacionJPADTO, Long> {

    @Query("select r from registro_cotizacion r where r.player.id = :playerId " +
            "and r.createdAt >= :inicio and r.createdAt <= :fin")
    List<RegistroCotizacionJPADTO> recuperarRegistrosDeJugador(
            @Param("playerId") Long playerId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}