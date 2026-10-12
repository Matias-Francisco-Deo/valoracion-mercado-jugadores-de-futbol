package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EstrategiaCotizacionDAOJPA extends JpaRepository<EstrategiaCotizacionJPADTO, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update estrategia_cotizacion e set e.factorEscala = :factorEscala where e.id = :id")
    void actualizar(@Param("id") Long id, @Param("factorEscala") Double factorEscala);

}