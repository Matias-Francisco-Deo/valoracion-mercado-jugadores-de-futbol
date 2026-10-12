package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionCotizacionesJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionCotizacionesDAOJPA extends JpaRepository<ConfiguracionCotizacionesJPADTO, Long> {

}