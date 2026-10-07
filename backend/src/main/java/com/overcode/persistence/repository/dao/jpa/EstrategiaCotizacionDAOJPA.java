package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.EstrategiaCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstrategiaCotizacionDAOJPA extends JpaRepository<EstrategiaCotizacionJPADTO, Long> {

}