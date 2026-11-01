package com.overcode.persistence.repository.dao.jpa;

import com.overcode.persistence.dto.jpa.cotizacion.ConfiguracionMercadoJPADTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionMercadoDAOJPA extends JpaRepository<ConfiguracionMercadoJPADTO, Long> {

}