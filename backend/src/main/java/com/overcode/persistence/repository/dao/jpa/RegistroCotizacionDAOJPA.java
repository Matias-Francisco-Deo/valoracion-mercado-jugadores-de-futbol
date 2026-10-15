package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.RegistroCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistroCotizacionDAOJPA extends JpaRepository<RegistroCotizacionJPADTO, Long> {

    @Query("select r from registro_cotizacion r where r.player.id = :playerId")
    List<RegistroCotizacionJPADTO> recuperarRegistrosDeJugador(@Param("playerId") Long playerId);
}