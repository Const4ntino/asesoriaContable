package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoObligacion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ObligacionNrusResponse {

    private Long id;
    private Long idCliente;
    private LocalDate periodoTributario;
    private String tipo;
    private BigDecimal monto;
    private LocalDate fechaLimite;
    private EstadoObligacion estado;
    private String observaciones;
}
