package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.ConfiguracionCotizaciones;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotNull
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
