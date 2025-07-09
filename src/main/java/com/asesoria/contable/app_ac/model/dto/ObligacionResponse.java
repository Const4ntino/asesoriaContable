package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ObligacionResponse {

    private Long id;

    private DeclaracionResponse declaracion;

    private ClienteResponse cliente;

    private String tipo;

    private LocalDate periodo;

    private BigDecimal monto;

    private LocalDate fechaLimite;

    private EstadoObligacion estado;

    private String observaciones;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;
}
