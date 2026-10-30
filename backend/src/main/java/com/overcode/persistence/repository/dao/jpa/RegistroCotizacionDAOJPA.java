package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.RegistroCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroCotizacionDAOJPA extends JpaRepository<RegistroCotizacionJPADTO, Long> {

    @Query(value = """
        select r.*
        from registros_cotizaciones r
        where r.player_id = :playerId
          and (cast(:inicio as timestamp) is null
               or r.created_at >= cast(:inicio as timestamp))
          and (cast(:fin as timestamp) is null
               or r.created_at <= cast(:fin as timestamp))
        """, nativeQuery = true)
    List<RegistroCotizacionJPADTO> recuperarRegistrosDeJugador(
            @Param("playerId") Long playerId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}