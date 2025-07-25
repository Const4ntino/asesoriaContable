package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoAlerta;
import com.asesoria.contable.app_ac.utils.enums.TipoAlertaCliente;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertaClienteResponse {

    private Long id;
    private Long idCliente;
    private String descripcion;
    private EstadoAlerta estado;
    private TipoAlertaCliente tipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
}
