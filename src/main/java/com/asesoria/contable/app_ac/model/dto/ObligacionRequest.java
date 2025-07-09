package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ObligacionRequest {

    private Long idDeclaracion;

    @NotNull
    private Long idCliente;

    @NotNull
    private String tipo;

    @NotNull
    private LocalDate periodo;

    @NotNull
    private BigDecimal monto;

    private LocalDate fechaLimite;

    private EstadoObligacion estado;

    private String observaciones;
}
