package com.overcode.persistence.dto.jpa.cotizacion;

import com.overcode.model.cotizacion.RegistroCotizacion;
import com.overcode.persistence.dto.jpa.PlayerJPADTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name="registro_cotizacion")
@Table(name = "registro_cotizaciones")
@Setter
@Getter
@NoArgsConstructor
public class RegistroCotizacionJPADTO {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    private PlayerJPADTO player;

    private LocalDate createdAt;

    private Double cotizacion;

    @OneToOne(fetch = FetchType.EAGER)
    @NotNull
    private EstrategiaCotizacionJPADTO estrategiaCotizacionUsada;


    public RegistroCotizacion aModelo() {
        RegistroCotizacion registroCotizacion = new RegistroCotizacion();
        registroCotizacion.setCotizacion(this.cotizacion);
        registroCotizacion.setCreatedAt(this.createdAt);
        registroCotizacion.setEstrategiaCotizacionUsada(this.estrategiaCotizacionUsada.aModelo());
        registroCotizacion.setId(this.id);
        registroCotizacion.setPlayer(this.player.aModelo());
        return registroCotizacion;
    }

    public static RegistroCotizacionJPADTO desdeModelo(RegistroCotizacion configuracion) {
        RegistroCotizacionJPADTO dto = new RegistroCotizacionJPADTO();
        EstrategiaCotizacionJPADTO estrategiaCotizacionUsada = EstrategiaCotizacionJPADTO.desdeModelo(configuracion.getEstrategiaCotizacionUsada());
        PlayerJPADTO player = PlayerJPADTO.desdeModelo(configuracion.getPlayer());

        dto.setCotizacion(configuracion.getCotizacion());
        dto.setCreatedAt(configuracion.getCreatedAt());
        dto.setEstrategiaCotizacionUsada(estrategiaCotizacionUsada);
        dto.setId(configuracion.getId());
        dto.setPlayer(player);
        return dto;
    }

}
