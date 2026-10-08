package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="configuracion_cotizaciones")
@Table(name = "configuraciones_cotizaciones")
@Setter
@Getter
@NoArgsConstructor
public class ConfiguracionCotizacionesJPADTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private EstrategiaCotizacionJPADTO estrategiaCotizacion;

    public ConfiguracionCotizaciones aModelo() {
        ConfiguracionCotizaciones configuracion = new ConfiguracionCotizaciones();
        configuracion.setId(getId());
        configuracion.setEstrategiaCotizacion(getEstrategiaCotizacion().aModelo());
        return configuracion;
    }

    public static ConfiguracionCotizacionesJPADTO desdeModelo(ConfiguracionCotizaciones configuracion) {
        ConfiguracionCotizacionesJPADTO dto = new ConfiguracionCotizacionesJPADTO();
        dto.setId(configuracion.getId());
        dto.setEstrategiaCotizacion(EstrategiaCotizacionJPADTO.desdeModelo(configuracion.getEstrategiaCotizacion()));
        return dto;
    }

}
