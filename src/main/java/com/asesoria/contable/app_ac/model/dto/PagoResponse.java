package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoPago;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PagoResponse {
    private Long id;
    private Long idObligacion;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private MedioPago medioPago;
    private String urlVoucher;
    private EstadoPago estado;
    private PagadoPor pagadoPor;
    private String comentarioContador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
