package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.RegistroCotizacionJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroCotizacionDAOJPA extends JpaRepository<RegistroCotizacionJPADTO, Long> {

}