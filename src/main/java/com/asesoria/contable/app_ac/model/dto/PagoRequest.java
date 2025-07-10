package com.asesoria.contable.app_ac.model.dto;

import com.asesoria.contable.app_ac.utils.enums.EstadoPago;
import com.asesoria.contable.app_ac.utils.enums.MedioPago;
import com.asesoria.contable.app_ac.utils.enums.PagadoPor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoRequest {

    @NotNull
    private Long idObligacion;

    @NotNull
    private BigDecimal montoPagado;

    @NotNull
    private LocalDate fechaPago;

    private MedioPago medioPago;

    private String urlVoucher;

    @NotNull
    private EstadoPago estado;

    @NotNull
    private PagadoPor pagadoPor;

    private String comentarioContador;
}
